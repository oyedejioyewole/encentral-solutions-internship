package org.library.book;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.library.book.model.Book;
import org.library.book.model.Student;
import org.library.book.service.LibraryService;
import org.library.book.util.HibernateUtil;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {
    private static Session session;
    private static LibraryService libraryService;
    private Transaction transaction;
    private Book testBook;
    private Student testStudent;

    @BeforeAll
    static void setUp () {
        session = HibernateUtil.getSessionFactory().openSession();
        libraryService = new LibraryService(session);
    }

    @AfterAll
    static void tearDown() {
        if (session != null) {
            session.close();
        }
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void beginTransaction() {
        transaction = session.beginTransaction();

        testBook = libraryService.createBook("9784561107828", "The Theory of Life and Human Psychology.", "Emmanuel Pfeifer");
        testStudent = libraryService.createStudent("JS3-AF44323", "Michael");

        session.flush();
    }

    @AfterEach
    void rollbackTransaction() {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }

        testBook = null;
        testStudent = null;
    }

    @Test
    void testDatabasePopulation() {
        assertInstanceOf(Book.class, testBook);
        assertNotNull(testBook, "Book isn't meant to be null");
        assertNotNull(testBook.getBookIsbn(), "Book ISBN isn't meant to be null");
        assertNotNull(testBook.getBookAuthor(), "Book title isn't meant to be null");
        assertNotNull(testBook.getBookAuthor(), "Book author isn't meant to be null");
        assertEquals("9784561107828", testBook.getBookIsbn());
        assertEquals("The Theory of Life and Human Psychology.", testBook.getBookTitle());
        assertEquals("Emmanuel Pfeifer", testBook.getBookAuthor());

        assertInstanceOf(Student.class, testStudent);
        assertNotNull(testStudent, "Student isn't meant to be null");
        assertNotNull(testStudent.getStudentId(), "Student ID isn't meant to be null");
        assertNotNull(testStudent.getStudentName(), "Student name isn't meant to be null");
        assertEquals("JS3-AF44323", testStudent.getStudentId());
        assertEquals("Michael", testStudent.getStudentName());
    }

    @Test
    void testBorrowBook() {
        Student anotherStudent = libraryService.createStudent("SS3-BC23455", "Angelina");

        Object borrowedBookOrStatus = libraryService.borrowBook(anotherStudent.getStudentId(), testBook.getBookIsbn());
        assertInstanceOf(Book.class, borrowedBookOrStatus);
        assertTrue(((Book) borrowedBookOrStatus).isBorrowed());

        borrowedBookOrStatus = libraryService.borrowBook(testStudent.getStudentId(), testBook.getBookIsbn());
        assertInstanceOf(String.class, borrowedBookOrStatus);
        assertEquals("book:already-borrowed", borrowedBookOrStatus);
    }

    @Test
    void testReturnBook() {
        Student anotherStudent = libraryService.createStudent("SS3-BC23455", "Angelina");

        Object borrowedBookOrStatus = libraryService.borrowBook(anotherStudent.getStudentId(), testBook.getBookIsbn());
        assertInstanceOf(Book.class, borrowedBookOrStatus);
        assertTrue(((Book) borrowedBookOrStatus).isBorrowed());

        String returnStatus = libraryService.returnBook(anotherStudent.getStudentId());
        assertEquals("returned", returnStatus);

        returnStatus = libraryService.returnBook(testStudent.getStudentId());
        assertEquals("student:no-borrow-records", returnStatus);

        borrowedBookOrStatus = libraryService.borrowBook(testStudent.getStudentId(), testBook.getBookIsbn());
        assertInstanceOf(Book.class, borrowedBookOrStatus);
        assertTrue(((Book) borrowedBookOrStatus).isBorrowed());
    }
}
