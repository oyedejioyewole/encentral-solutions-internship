package org.library.book.service;

import com.querydsl.jpa.impl.JPAQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.library.book.model.*;

public class LibraryService {
    private final Session session;
    private final Logger logger;
    private final QBook qBook;
    private final QStudent qStudent;
    private final QBorrowRecord qBorrowRecord;

    public LibraryService(Session session) {
        this.session = session;
        this.logger = LogManager.getLogger();

        this.qBook = QBook.book;
        this.qStudent = QStudent.student;
        this.qBorrowRecord = QBorrowRecord.borrowRecord;
    }

    public Student createStudent(String studentId, String studentName) {
        Student student = new Student(studentId, studentName);
        session.persist(student);

        logger.info("Saved {} to in-memory database", student);
        return student;
    }

    public Book createBook(String bookIsbn, String bookTitle, String bookAuthor) {
        Book book = new Book(bookIsbn, bookTitle, bookAuthor);
        session.persist(book);

        logger.info("Saved {} to in-memory database", book);
        return book;
    }

    public Object borrowBook(String studentId, String bookIsbn) {
        Student foundStudent =  new JPAQuery<Void>(session).select(qStudent).from(qStudent).where(qStudent.studentId.eq(studentId)).fetchOne();

        if (foundStudent == null) {
            logger.warn("Student with ID '{}' couldn't be found", studentId);
            return "student:not-found";
        }

        Long activeCount =  new JPAQuery<Void>(session).select(qBorrowRecord.count()).from(qBorrowRecord).where(qBorrowRecord.studentThatBorrowed.eq(foundStudent).and(qBorrowRecord.isReturned.isFalse())).fetchOne();

        if (activeCount != null && activeCount > 0) {
            logger.warn("Student with ID '{}' has borrowed a book before", studentId);
            return "student:has-borrowed";
        }

        Book foundBook =  new JPAQuery<Void>(session).select(qBook).from(qBook).where(qBook.bookIsbn.eq(bookIsbn)).fetchOne();

        if (foundBook == null) {
            logger.warn("The book with ISBN '{}' couldn't be found", bookIsbn);
            return "book:not-found";
        }

        if (foundBook.isBorrowed()) {
            logger.warn("The book with ISBN '{}' has been borrowed already", bookIsbn);
            return "book:already-borrowed";
        }

        foundBook.setBorrowed(true);
        BorrowRecord borrowRecord = new BorrowRecord(foundStudent, foundBook);
        session.persist(borrowRecord);

        logger.info(borrowRecord);

        return foundBook;
    }

    public String returnBook(String studentId) {
        Student foundStudent = new JPAQuery<Void>(session).select(qStudent).from(qStudent).where(qStudent.studentId.eq(studentId)).fetchOne();

        if (foundStudent == null) {
            logger.warn("Student with ID '{}' couldn't be found", studentId);
            return "student:not-found";
        }
        BorrowRecord foundBorrowRecord = new JPAQuery<Void>(session).select(qBorrowRecord).from(qBorrowRecord).where(qBorrowRecord.studentThatBorrowed.eq(foundStudent).and(qBorrowRecord.isReturned.isFalse())).fetchOne();

        if (foundBorrowRecord == null) {
            logger.warn("Student hasn't borrowed this book");
            return "student:no-borrow-records";
        }

        foundBorrowRecord.setIsReturned(true);
        logger.info(foundBorrowRecord);

        return "returned";
    }
}
