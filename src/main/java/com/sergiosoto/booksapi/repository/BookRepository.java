package com.sergiosoto.booksapi.repository;

import com.sergiosoto.booksapi.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);

    Optional<Book> findByIsbn(String s);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByPublisherContainingIgnoreCase(String publisher);

    List<Book> findByYearOfPublishing(int yop);
}
