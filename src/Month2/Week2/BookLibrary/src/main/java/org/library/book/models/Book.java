package org.library.book.models;

import java.time.LocalDateTime;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final int copyNumber;
    private boolean isAvailable;
    private String borrowedBy;
    private LocalDateTime borrowedAt;

    public Book(String isbn, String title, String author, int copyNumber) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copyNumber = copyNumber;
        this.isAvailable = true;
        this.borrowedBy = null;
        this.borrowedAt = null;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getCopyNumber() { return copyNumber; }
    public boolean isAvailable() { return isAvailable; }
    public String getBorrowedBy() { return borrowedBy; }
    public LocalDateTime getBorrowedAt() { return borrowedAt; }

    // Extra logic
    public void borrowBook(String userId) {
        if (!isAvailable) {
            throw new IllegalStateException("Book is already borrowed");
        }
        this.isAvailable = false;
        this.borrowedBy = userId;
        this.borrowedAt = LocalDateTime.now();
    }

    public void returnBook() {
        this.isAvailable = true;
        this.borrowedBy = null;
        this.borrowedAt = null;
    }

    public String getUniqueId() {
        return isbn + "-" + copyNumber;
    }

    @Override
    public int hashCode() {
        return isbn.hashCode() + copyNumber;
    }
}
