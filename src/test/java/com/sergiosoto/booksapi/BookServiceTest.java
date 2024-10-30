package com.sergiosoto.booksapi;

import com.sergiosoto.booksapi.model.Book;
import com.sergiosoto.booksapi.repository.BookRepository;
import com.sergiosoto.booksapi.service.BookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)

public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookService bookService;

    //Normalize ISBN tests
    @Test
    public void normalizeIsbn_validIsbn_returnsNormalizedIsbn() {
        String isbn = "9783161484100";
        String expectedNormalizedIsbn = "978-3-161-48410-0";
        final String normalizedIsbn = bookService.normalizeIsbn(isbn);
        Assertions.assertEquals(expectedNormalizedIsbn, normalizedIsbn);
    }

    @Test
    public void normalizeIsbn_invalidIsbn_throwsIllegalArgumentException() {
        String isbn = "1234567890123";
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.normalizeIsbn(isbn));
    }
    @Test
    public void normalizeIsbn_isbnWithSpaces_returnsNormalizedIsbn(){
        String isbn = "978 3 161 48410 0";
        String expectedNormalizedIsbn = "978-3-161-48410-0";
        final String normalizedIsbn = bookService.normalizeIsbn(isbn);
        Assertions.assertEquals(expectedNormalizedIsbn, normalizedIsbn);
    }

    //validateBook tests

    @Test
    public void validateBook_validBook_returnsEmptyOptional(){
        Book book = new Book("title", "Author1", "978-3-161-48410-0", "Publisher1", 2021, "Genre1", 300, 19.99);
        Optional<String> result = bookService.validateBook(book);
        Assertions.assertTrue(result.isEmpty());
    }
    @Test
    public void validateBook_invalidYear_returnsErrorMessage(){
        Book book = new Book("title", "Author1", "978-3-161-48410-0", "Publisher1", -1000, "Genre1", 300, 19.99);
        Optional<String> result = bookService.validateBook(book);
        Assertions.assertEquals("Year of publishing must be between 1 and 2100.", result.get());
    }
}
