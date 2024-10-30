package com.sergiosoto.booksapi.service;

import com.sergiosoto.booksapi.model.Book;
import com.sergiosoto.booksapi.repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public String normalizeIsbn(String isbn) {
        String cleanedIsbn = isbn.replaceAll("[-\\s]", "");
        if (isInvalidIsbn(cleanedIsbn)) {
            throw new IllegalArgumentException("Invalid ISBN format. It must be a 13-digit ISBN starting with '978' or '979'.");
        }
        return cleanedIsbn.substring(0, 3) + "-" + cleanedIsbn.charAt(3) + "-" + cleanedIsbn.substring(4, 7) + "-" + cleanedIsbn.substring(7, 12) + "-" + cleanedIsbn.charAt(12);
    }

    public ResponseEntity<Object> createBook(Book book) {
        Optional<String> validationError = validateBook(book);
        if (validationError.isPresent()) {
            return ResponseEntity.badRequest().body(validationError.get());
        }

        String normalizedIsbn = normalizeIsbn(book.getIsbn());
        if (bookRepository.findByIsbn(normalizedIsbn).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Book with ISBN " + normalizedIsbn + " already exists.");
        }

        book.setIsbn(normalizedIsbn);
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    public boolean isStringInvalid(String input) {
        return input == null || input.isBlank();
    }

    public Optional<String> validateBook(Book book) {
        if (isStringInvalid(book.getTitle())) return Optional.of("Title is required.");
        if (isStringInvalid(book.getIsbn())) return Optional.of("ISBN is required.");
        if (isInvalidIsbn(book.getIsbn())) return Optional.of("ISBN format is invalid.");
        if (isStringInvalid(book.getAuthor())) return Optional.of("Author is required.");
        if (isStringInvalid(book.getPublisher())) return Optional.of("Publisher is required.");
        if (book.getYearOfPublishing() <= 0 || book.getYearOfPublishing() > 2100) {
            return Optional.of("Year of publishing must be between 1 and 2100.");
        }
        if (book.getPageCount() <= 0) return Optional.of("Page count must be greater than 0.");
        if (book.getPrice() < 0) return Optional.of("Price cannot be negative.");
        if (isStringInvalid(book.getGenre())) return Optional.of("Genre is required.");

        return Optional.empty();
    }

    private boolean isInvalidIsbn(String isbn) {
        String cleanedIsbn = isbn.replaceAll("[-\\s]", "");
        String isbn13Regex = "^97[8-9][0-9]{10}$";
        return !cleanedIsbn.matches(isbn13Regex);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public ResponseEntity<Book> getBookById(Long id) {
        return bookRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<List<Book>> getBooksByAuthor(String author) {
        if (isStringInvalid(author)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author);
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    public ResponseEntity<List<Book>> getBooksByPublisher(String publisher) {
        if (isStringInvalid(publisher)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Book> books = bookRepository.findByPublisherContainingIgnoreCase(publisher);
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    public ResponseEntity<List<Book>> getBooksByTitle(String title) {
        if (isStringInvalid(title)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    public ResponseEntity<List<Book>> getBooksByYearOfPublishing(int yop) {
        if (yop <= 0 || yop > 2100) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Book> books = bookRepository.findByYearOfPublishing(yop);
        return books.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(books);
    }

    public ResponseEntity<List<Book>> getBooksByGenre(String genre) {
        if (isStringInvalid(genre)) {
            return ResponseEntity.badRequest().build();
        }
        List<Book> books = bookRepository.findByGenreContainingIgnoreCase(genre);
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    public ResponseEntity<Book> getBookByIsbn(String isbn) {
        if (isStringInvalid(isbn)) {
            return ResponseEntity.badRequest().build();
        }
        return bookRepository.findByIsbn(normalizeIsbn(isbn)).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    public ResponseEntity<Object> updateBook(Long id, @Valid Book updatedBook) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");
        }

        String normalizedIsbn = normalizeIsbn(updatedBook.getIsbn());


        Optional<Book> existingBookWithIsbn = bookRepository.findByIsbn(normalizedIsbn);
        if (existingBookWithIsbn.isPresent() && !existingBookWithIsbn.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Another book with ISBN " + normalizedIsbn + " already exists.");
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

    public ResponseEntity<String> deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
