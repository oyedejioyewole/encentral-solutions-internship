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

    public BorrowRecord borrowBook(String studentId, String bookIsbn) {
        Student foundStudent =  new JPAQuery<Void>(session).select(qStudent).from(qStudent).where(qStudent.studentId.eq(studentId)).fetchOne();

        if (foundStudent == null) {
            String message = String.format("Student with ID '%s' couldn't be found", studentId);

            logger.warn(message);
            throw new IllegalAccessError(message);
        }

        Book foundBook =  new JPAQuery<Void>(session).select(qBook).from(qBook).where(qBook.bookIsbn.eq(bookIsbn)).fetchOne();

        if (foundBook == null) {
            String message = String.format("The book with ISBN '%s' couldn't be found", bookIsbn);

            logger.warn(message);
            throw new IllegalAccessError(message);
        }

        if (foundBook.isBorrowed()) {
            String message = String.format("The book with ISBN '%s' has been borrowed already", bookIsbn);

            logger.warn(message);
            throw new IllegalStateException(message);
        }

        Long activeCount =  new JPAQuery<Void>(session).select(qBorrowRecord.count()).from(qBorrowRecord).where(qBorrowRecord.studentThatBorrowed.eq(foundStudent).and(qBorrowRecord.isReturned.isFalse())).fetchOne();

        if (activeCount != null && activeCount > 0) {
            String message = String.format("Student with ID '%s' has borrowed a book before", studentId);

            logger.warn(message);
            throw new IllegalStateException(message);
        }

        foundBook.setBorrowed(true);
        BorrowRecord borrowRecord = new BorrowRecord(foundStudent, foundBook);
        session.persist(borrowRecord);

        logger.info(borrowRecord);
        return borrowRecord;
    }

    public void returnBook(String studentId) {
        Student foundStudent = new JPAQuery<Void>(session).select(qStudent).from(qStudent).where(qStudent.studentId.eq(studentId)).fetchOne();

        if (foundStudent == null) {
            String message = String.format("Student with ID '%s' couldn't be found", studentId);

            logger.warn(message);
            throw new IllegalAccessError(message);
        }
        BorrowRecord foundBorrowRecord = new JPAQuery<Void>(session).select(qBorrowRecord).from(qBorrowRecord).where(qBorrowRecord.studentThatBorrowed.eq(foundStudent).and(qBorrowRecord.isReturned.isFalse())).fetchOne();

        if (foundBorrowRecord == null) {
            String message = "Student hasn't borrowed this book";

            logger.warn(message);
            throw new IllegalStateException(message);
        }

        foundBorrowRecord.setIsReturned(true);
        session.persist(foundBorrowRecord);
        logger.info(foundBorrowRecord);
    }
}
