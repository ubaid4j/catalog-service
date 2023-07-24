package dev.ubaid.catalogservice.web;

import dev.ubaid.catalogservice.config.SecurityConfig;
import dev.ubaid.catalogservice.domain.BookNotFoundException;
import dev.ubaid.catalogservice.domain.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookController.class)
@Import(SecurityConfig.class)
public class BookControllerMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @SuppressWarnings("unused")
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void whenGetBookNotExistingTheShouldReturn404() throws Exception {
        String isbn = "1234567890";
        given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);
        mockMvc
                .perform(get("/api/books/" + isbn)
                        .with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_employee")))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteWithEmployeeRoleThenShouldReturn204() throws Exception {
        String isbn = "1234567890";
        doNothing().when(bookService).removeBookFromCatalog(isbn);
        mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/books/" + isbn)
                        .with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_employee"))
                        )
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void whenDeleteBookWithCustomerRoleThenShouldReturn403() throws Exception {
        String isbn = "1234567890";
        mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/books/" + isbn)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_customer")))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void whenDeleteBookNotAuthenticatedThenShouldReturn401() throws Exception {
        String isbn = "1234567890";
        mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/books/" + isbn))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
