package org.library.book.service;

import com.querydsl.jpa.impl.JPAQuery;
import org.hibernate.Session;
import org.library.book.model.*;

public class LibraryService {
    private final Session session;
    private final QBook qBook = QBook.book;
    private final QStudent qStudent = QStudent.student;
    private final QBorrowRecord qBorrowRecord = QBorrowRecord.borrowRecord;

    public LibraryService(Session session) {
        this.session = session;
    }

    public Student createStudent(String studentId, String studentName) {
        Student student = new Student(studentId, studentName);
        session.persist(student);

        return student;
    }

    public Book createBook(String bookIsbn, String bookTitle, String bookAuthor) {
        Book book = new Book(bookIsbn, bookTitle, bookAuthor);
        session.persist(book);

        return book;
    }

    public String borrowBook(String studentId, String bookIsbn) {
        Student foundStudent =  new JPAQuery<Void>(session).select(qStudent).from(qStudent).where(qStudent.studentId.eq(studentId)).fetchOne();

        if (foundStudent == null) return "student:not-found";

        Long activeCount =  new JPAQuery<Void>(session).select(qBorrowRecord.count()).from(qBorrowRecord).where(qBorrowRecord.studentThatBorrowed.eq(foundStudent).and(qBorrowRecord.isReturned.isFalse())).fetchOne();

        if (activeCount != null && activeCount > 0) {
            return "student:has-borrowed";
        }

        Book foundBook =  new JPAQuery<Void>(session).select(qBook).from(qBook).where(qBook.bookIsbn.eq(bookIsbn)).fetchOne();

        if (foundBook == null) {
            return "book:not-found";
        }

        if (foundBook.isBorrowed()) {
            return "book:already-borrowed";
        }

        foundBook.setBorrowed(true);
        BorrowRecord borrowRecord = new BorrowRecord(foundStudent, foundBook);
        session.persist(borrowRecord);

        return "borrowed";
    }

    public String returnBook(String studentId) {
        Student foundStudent = new JPAQuery<Void>(session).select(qStudent).from(qStudent).where(qStudent.studentId.eq(studentId)).fetchOne();

        if (foundStudent == null) {
            return "student:not-found";
        }

        System.out.println(foundStudent);

        BorrowRecord foundBorrowRecord = new JPAQuery<Void>(session).select(qBorrowRecord).from(qBorrowRecord).where(qBorrowRecord.studentThatBorrowed.eq(foundStudent).and(qBorrowRecord.isReturned.isFalse())).fetchOne();

        if (foundBorrowRecord == null) {
            return "student:no-borrow-records";
        }

        System.out.println(foundBorrowRecord);

        foundBorrowRecord.setIsReturned(true);
        Book borrowedBook = foundBorrowRecord.getBookThatWasBorrowed();
        borrowedBook.setBorrowed(false);

        return "returned";
    }
}
