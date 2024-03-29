package dev.ubaid.catalogservice.demo;

import dev.ubaid.catalogservice.domain.Book;
import dev.ubaid.catalogservice.domain.BookRepository;
import java.util.List;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("testdata")
public class BookDataLoader {
    private final BookRepository bookRepository;
    
    public BookDataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void loadBookTestData() {
        bookRepository.deleteAll();
        
        var book1 = Book.of("1234567890", "T1", "author1", 10.1);
        var book2 = Book.of("1234567891", "T2", "author2", 11.1);
        bookRepository.saveAll(List.of(book1, book2));
    }
}
