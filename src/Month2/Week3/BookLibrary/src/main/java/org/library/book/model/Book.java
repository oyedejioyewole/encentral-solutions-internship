package org.library.book.model;

import jakarta.persistence.*;

@Entity
public class Book {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "book_id")
    private long bookId;

    @Column (name = "book_isbn")
    private String bookIsbn;

    @Column (name = "book_title")
    private String bookTitle;

    @Column (name = "book_author")
    private String bookAuthor;

    @Column (name = "is_borrowed")
    private boolean isBorrowed;

    public Book(String isbn, String title, String author) {
        this.bookIsbn = isbn;
        this.bookTitle = title;
        this.bookAuthor = author;
        this.isBorrowed = false;
    }

    public Book() {}

    public String getBookIsbn() { return this.bookIsbn; }
    public String getBookTitle() { return this.bookTitle; }
    public String getBookAuthor() { return this.bookAuthor; }
    public boolean isBorrowed() { return this.isBorrowed; }

    public void setBorrowed (boolean status) { isBorrowed = status; }

    @Override
    public String toString() {
        return String.format("Book{isbn='%s', title='%s', author='%s', isBorrowed=%b}", this.bookIsbn, this.bookTitle, this.bookAuthor, this.isBorrowed);
    }
}