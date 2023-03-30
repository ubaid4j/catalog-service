package dev.ubaid.catalogservice.domain;

import java.util.concurrent.ThreadLocalRandom;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookService {
    
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    @SneakyThrows
    public Iterable<Book> viewBookList() {
        Thread.sleep(ThreadLocalRandom.current().nextInt(6000));
        return bookRepository.findAll();
    }
    
    public Book viewBookDetails(String isbn) {
        return bookRepository.findByIsbn(isbn)
            .orElseThrow(() -> new BookNotFoundException(isbn));
    }
    
    public Book addBookToCatalog(Book book) {
        if (bookRepository.existsByIsbn(book.isbn())) {
            throw new BookAlreadyExistsException(book.isbn());
        }
        return bookRepository.save(book);
    }
    
    public void removeBookFromCatalog(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }
    
    public Book editBookDetails(String isbn, Book book) {
        return bookRepository.findByIsbn(isbn)
            .map(existingBook -> {
                var bookToUpdate = new Book(
                    existingBook.id(), 
                    existingBook.isbn(),
                    book.title(),
                    book.author(),
                    book.price(),
                    book.publisher(),
                    existingBook.createdDate(),
                    existingBook.lastModifiedDate(),
                    existingBook.version());
                return bookRepository.save(bookToUpdate);
            })
            .orElseGet(() -> addBookToCatalog(book));
    }
}
