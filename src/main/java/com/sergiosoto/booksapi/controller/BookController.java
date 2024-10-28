package com.sergiosoto.booksapi.controller;

import com.sergiosoto.booksapi.model.Book;
import com.sergiosoto.booksapi.repository.BookRepository;
import com.sergiosoto.booksapi.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/books")
public class BookController {

    private final BookRepository bookRepository;
    private final BookService bookService;

    @Autowired
    public BookController(BookRepository bookRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Object> createBook(@Valid @RequestBody Book book) {
        Optional<String> validationError = bookService.validateBook(book);
        if (validationError.isPresent()) {
            return ResponseEntity.badRequest().body(validationError.get());
        }

        String normalizedIsbn = bookService.normalizeIsbn(book.getIsbn());
        if (bookRepository.findByIsbn(normalizedIsbn).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Book with ISBN " + normalizedIsbn + " already exists.");
        }

        book.setIsbn(normalizedIsbn);
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        if (bookService.isStringInvalid(author)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author);
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }


    @GetMapping("/publisher/{publisher}")
    public ResponseEntity<List<Book>> getBooksByPublisher(@PathVariable String publisher) {
        if (bookService.isStringInvalid(publisher)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Book> books = bookRepository.findByPublisherContainingIgnoreCase(publisher);
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> getBooksByTitle(@PathVariable String title) {
        if (bookService.isStringInvalid(title)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }


    @GetMapping("/yop/{yop}")
    public ResponseEntity<List<Book>> getBooksByYearOfPublishing(@PathVariable int yop) {
        if (yop <= 0 || yop > 2100) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Book> books = bookRepository.findByYearOfPublishing(yop);
        return books.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(books);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<Book> getBookByGenre(@PathVariable String genre) {
        if (bookService.isStringInvalid(genre)) {
            return ResponseEntity.badRequest().build();
        }
        return bookRepository.findByIsbn(bookService.normalizeIsbn(genre))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        if (bookService.isStringInvalid(isbn)) {
            return ResponseEntity.badRequest().build();
        }
        return bookRepository.findByIsbn(bookService.normalizeIsbn(isbn))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable Long id, @Valid @RequestBody Book updatedBook) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");
        }

        String normalizedIsbn = bookService.normalizeIsbn(updatedBook.getIsbn());


        Optional<Book> existingBookWithIsbn = bookRepository.findByIsbn(normalizedIsbn);
        if (existingBookWithIsbn.isPresent() && !existingBookWithIsbn.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Another book with ISBN " + normalizedIsbn + " already exists.");
        }

        Book actualBook = optionalBook.get();
        actualBook.setTitle(updatedBook.getTitle());
        actualBook.setAuthor(updatedBook.getAuthor());
        actualBook.setPublisher(updatedBook.getPublisher());
        actualBook.setYearOfPublishing(updatedBook.getYearOfPublishing());
        actualBook.setIsbn(normalizedIsbn);
        actualBook.setGenre(updatedBook.getGenre());
        actualBook.setPageCount(updatedBook.getPageCount());
        actualBook.setPrice(updatedBook.getPrice());

        Book savedBook = bookRepository.save(actualBook);
        return ResponseEntity.ok(savedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);

        if (optionalBook.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        bookRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


}

