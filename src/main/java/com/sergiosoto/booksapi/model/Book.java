package com.sergiosoto.booksapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "The title must not be blank")
    private String title;
    @NotBlank(message = "The author must not be blank")
    private String author;
    @NotBlank(message = "The ISBN must not be blank")
    @Pattern(regexp = "^97[8|9]-[0-9]-[0-9]{1,7}-[0-9]{1,7}-[0-9]$",
            message = "The ISBN does not have a valid format it most follow this format: '978-X-XXXXXXX-XXXX-X'.")
    private String isbn;
    @NotBlank(message = "The publisher must not be blank")
    private String publisher;
    private int yearOfPublishing;
    @NotBlank(message = "The genre must not be blank")
    private String genre;
    @NotNull(message = "Page count cannot be null")
    @Min(value = 1, message = "Page count must be at least 1")
    private int pageCount;
    @NotNull(message = "The price must not be null")
    @Min(value =1, message = "Price must be at least 1")
    private double price;

    public Book(){

    }

    public Book( String title, String author, String isbn, String publisher, int yearOfPublishing, String genre, int pageCount, double price) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.yearOfPublishing = yearOfPublishing;
        this.genre = genre;
        this.pageCount = pageCount;
        this.price = price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getYearOfPublishing() {
        return yearOfPublishing;
    }

    public void setYearOfPublishing(int yearOfPublishing) {
        this.yearOfPublishing = yearOfPublishing;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
