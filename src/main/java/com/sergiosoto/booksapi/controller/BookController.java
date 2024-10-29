package com.sergiosoto.booksapi.controller;

import com.sergiosoto.booksapi.model.Book;
import com.sergiosoto.booksapi.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Object> createBook(@Valid @RequestBody Book book) {
        return bookService.createBook(book);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);

    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        return bookService.getBooksByAuthor(author);
    }


    @GetMapping("/publisher/{publisher}")
    public ResponseEntity<List<Book>> getBooksByPublisher(@PathVariable String publisher) {
        return bookService.getBooksByPublisher(publisher);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> getBooksByTitle(@PathVariable String title) {
        return bookService.getBooksByTitle(title);
    }


    @GetMapping("/yop/{yop}")
    public ResponseEntity<List<Book>> getBooksByYearOfPublishing(@PathVariable int yop) {
        return bookService.getBooksByYearOfPublishing(yop);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getBooksByGenre(@PathVariable String genre) {
        return bookService.getBooksByGenre(genre);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable Long id, @Valid @RequestBody Book updatedBook) {
        return bookService.updateBook(id, updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id);
    }


}

