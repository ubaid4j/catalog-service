package dev.ubaid.catalogservice.web;

import dev.ubaid.catalogservice.domain.Book;
import dev.ubaid.catalogservice.domain.BookService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    @GetMapping
    public Iterable<Book> getAll() {
        log.info("REST request to get all books in the catalog");
        return bookService.viewBookList();
    }
    
    @GetMapping("{isbn}")
    public Book getByIsbn(@PathVariable String isbn) {
        log.info("REST request to get book by ISBN {}", isbn);
        return bookService.viewBookDetails(isbn);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@Valid @RequestBody Book book) {
        log.info("REST request to add book: {}", book);
        return bookService.addBookToCatalog(book);
    }
    
    @DeleteMapping("{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String isbn) {
        log.info("REST request to remove book from isbn {}", isbn);
        bookService.removeBookFromCatalog(isbn);
    }
    
    @PutMapping("{isbn}")
    public Book updateOrCreate(@Valid @PathVariable String isbn, @RequestBody Book book) {
        log.info("REST request to edit book detail: {}", book);
        return bookService.editBookDetails(isbn, book);
    }
}
