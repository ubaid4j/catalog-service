package dev.ubaid.catalogservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.ubaid.catalogservice.config.DataConfig;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(
    replace = Replace.NONE
)
@ActiveProfiles("integration")
public class BookRepositoryJdbcTests {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;
    
    @Test
    void findBookByIsbnWhenExisting() {
        var bookIsbn = "1234567892";
        var book = Book.of(bookIsbn, "title", "ubaid", 231.1);
        jdbcAggregateTemplate.insert(book);
        Optional<Book> actualBook = bookRepository.findByIsbn(bookIsbn);
        
        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().isbn()).isEqualTo(book.isbn());
    }
    
    @Test
    void whenCreateBookNotAuthenticatedThenNoAuditMetaData() {
        Book bookToCreate = Book.of("1234567893", "Title", "ubaid", 25.1);
        Book createdBook = bookRepository.save(bookToCreate);
        assertThat(createdBook.createdBy()).isNull();
        assertThat(createdBook.lastModifiedBy()).isNull();;
    }
    
    @Test
    @WithMockUser("ubaid")
    void whenCreateBookAuthenticatedThenAuditMetaData() {
        Book bookToCreate = Book.of("1234567894", "Title", "Author", 342.0);
        Book createdBook = bookRepository.save(bookToCreate);
        assertThat(createdBook.createdBy()).isEqualTo("ubaid");
        assertThat(createdBook.lastModifiedBy()).isEqualTo("ubaid");
    }
}
