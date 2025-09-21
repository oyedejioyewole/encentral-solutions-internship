package org.library.book.model;

import jakarta.persistence.*;

@Entity
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "borrow_record_id")
    private long borrowRecordId;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student studentThatBorrowed;

    @ManyToOne
    @JoinColumn(name = "book_isbn")
    private Book bookThatWasBorrowed;

    @Column (name = "is_returned")
    private boolean isReturned;

    public BorrowRecord(Student student, Book book) {
        this.studentThatBorrowed = student;
        this.bookThatWasBorrowed = book;
        this.isReturned = false;
    }

    public BorrowRecord() {
        this.isReturned = false;
    }

    public Book getBookThatWasBorrowed() {
        return this.bookThatWasBorrowed;
    }

    public void setIsReturned(boolean isReturned) { this.isReturned = isReturned; }

    @Override
    public String toString() {
        return String.format("BorrowRecord{id='%s', student: %s, book: %s, isReturned: %b}", this.borrowRecordId, this.studentThatBorrowed, this.bookThatWasBorrowed, this.isReturned);
    }
}
