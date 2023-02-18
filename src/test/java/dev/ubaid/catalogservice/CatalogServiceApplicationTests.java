package dev.ubaid.catalogservice;

import static org.assertj.core.api.Assertions.assertThat;

import dev.ubaid.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
class CatalogServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;
    
    @Test
    void contextLoads() {
    }
    
    @Test
    void whenPostRequestThenBookCreated() {
        var expectedBook = Book.of("1234567890", "Title", "Author", 10.11);
        
        webTestClient
            .post()
            .uri("/api/books")
            .bodyValue(expectedBook)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Book.class).value(actualBook -> {
                assertThat(actualBook).isNotNull();
                assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
            });
    }

}
