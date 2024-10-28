package com.sergiosoto.booksapi.service;

import com.sergiosoto.booksapi.model.Book;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {

    public String normalizeIsbn(String isbn) {
        String cleanedIsbn = isbn.replaceAll("[-\\s]", "");
        if (!isValidIsbn(cleanedIsbn)) {
            throw new IllegalArgumentException("Invalid ISBN format. It must be a 13-digit ISBN starting with '978' or '979'.");
        }
        return cleanedIsbn.substring(0, 3) + "-" +
                cleanedIsbn.charAt(3) + "-" +
                cleanedIsbn.substring(4, 10) + "-" +
                cleanedIsbn.substring(10, 12) + "-" +
                cleanedIsbn.charAt(12);
    }
    public boolean isStringInvalid(String input) {
        return input == null || input.isBlank();
    }
    public Optional<String> validateBook(Book book) {
        if (isStringInvalid(book.getTitle())) return Optional.of("Title is required.");
        if (isStringInvalid(book.getIsbn())) return Optional.of("ISBN is required.");
        if (!isValidIsbn(book.getIsbn())) return Optional.of("ISBN format is invalid.");
        if (isValidIsbn(book.getAuthor())) return Optional.of("Author is required.");
        if (isValidIsbn(book.getPublisher())) return Optional.of("Publisher is required.");
        if (book.getYearOfPublishing() <= 0 || book.getYearOfPublishing() > 2100) {
            return Optional.of("Year of publishing must be between 1 and 2100.");
        }
        if (book.getPageCount() <= 0) return Optional.of("Page count must be greater than 0.");
        if (book.getPrice() < 0) return Optional.of("Price cannot be negative.");
        if (isValidIsbn(book.getGenre())) return Optional.of("Genre is required.");

        return Optional.empty();
    }

    private boolean isValidIsbn(String isbn) {
        String cleanedIsbn = isbn.replaceAll("[-\\s]", "");
        String isbn13Regex = "^97[89][0-9]{10}$";
        return cleanedIsbn.matches(isbn13Regex);
    }

}
