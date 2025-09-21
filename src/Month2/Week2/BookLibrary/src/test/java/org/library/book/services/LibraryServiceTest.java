package org.library.book.services;

import org.library.book.models.*;
import org.library.book.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

class LibraryServiceTest {
    private BookRepository bookRepository;

    private LibraryService libraryService;
    private Teacher teacher;
    private Book testBook;

    @BeforeEach
    void setUp() {
        bookRepository = BookRepository.getInstance();
        UserRepository userRepository = UserRepository.getInstance();

        libraryService = new LibraryService(bookRepository, userRepository);
        teacher = new Teacher("T001", "Dr. Smith", "Computer Science");
        testBook = new Book("978-123456789", "Test Book", "Test Author", 1);
    }

    @Test
    @DisplayName("Should successfully borrow available book")
    void shouldBorrowAvailableBook() {
        // Given
        String bookIsbn = "978-123456789";
        when(bookRepository.existsByIsbn(bookIsbn)).thenReturn(true);
        when(bookRepository.findBorrowedByUser(teacher.getId())).thenReturn(Collections.emptyList());
        when(bookRepository.findAvailableByIsbn(bookIsbn)).thenReturn(Collections.singletonList(testBook));

        // When
        String result = libraryService.borrowBook(bookIsbn, teacher);

        // Then
        assertThat(result).contains("Book borrowed successfully");
        verify(testBook).borrowBook(teacher.getId());
    }

    @Test
    @DisplayName("Should return 'book taken' when no copies available")
    void shouldReturnBookTakenWhenNoCopiesAvailable() {
        // Given
        String bookIsbn = "978-123456789";
        assertTrue(bookRepository.existsByIsbn(bookIsbn));


        when(bookRepository.findBorrowedByUser(teacher.getId())).thenReturn(Collections.emptyList());
        when(bookRepository.findAvailableByIsbn(bookIsbn)).thenReturn(Collections.emptyList());

        // When
        String result = libraryService.borrowBook(bookIsbn, teacher);

        // Then
        assertThat(result).contains("book taken");
    }

    @Test
    @DisplayName("Should reject borrowing request for non-existent book")
    void shouldRejectBorrowingNonExistentBook() {
        // Given
        String bookIsbn = "978-123456789";
        assertTrue(bookRepository.existsByIsbn(bookIsbn));

        // When
        String result = libraryService.borrowBook(bookIsbn, teacher);

        // Then
        assertThat(result).contains("Book not found");
    }

    @Test
    @DisplayName("Should reject borrowing if user already has the book")
    void shouldRejectIfUserAlreadyHasBook() {
        // Given
        String bookIsbn = "978-123456789";
        Book alreadyBorrowed = new Book("978-123456789", bookIsbn, "Test Author", 1);
        when(bookRepository.existsByIsbn(bookIsbn)).thenReturn(true);
        when(bookRepository.findBorrowedByUser(teacher.getId())).thenReturn(List.of(alreadyBorrowed));

        // When
        String result = libraryService.borrowBook(bookIsbn, teacher);

        // Then
        assertThat(result).contains("You already have a copy of this book");
    }

    @Test
    @DisplayName("Should handle null book title")
    void shouldHandleNullBookIsbn() {
        // When
        String result = libraryService.borrowBook(null, teacher);

        // Then
        assertThat(result).contains("Invalid book title");
    }

    @Test
    @DisplayName("Should handle empty book title")
    void shouldHandleEmptyBookIsbn() {
        // When
        String result = libraryService.borrowBook("   ", teacher);

        // Then
        assertThat(result).contains("Invalid book title");
    }

    @Test
    @DisplayName("Should handle null user")
    void shouldHandleNullUser() {
        // When
        String result = libraryService.borrowBook("Test Book", null);

        // Then
        assertThat(result).contains("Invalid user");
    }

    @Test
    @DisplayName("Should successfully return borrowed book")
    void shouldReturnBorrowedBook() {
        // Given
        String bookIsbn = "978-123456789";
        Book borrowedBook = spy(new Book("978-123456789", bookIsbn, "Test Author", 1));
        when(bookRepository.findBorrowedByUser(teacher.getId())).thenReturn(Collections.singletonList(borrowedBook));

        // When
        boolean result = libraryService.returnBook(bookIsbn, teacher);

        // Then
        assertTrue(result);
        verify(borrowedBook).returnBook();
    }

    @Test
    @DisplayName("Should return false when user doesn't have the book")
    void shouldReturnFalseWhenUserDoesntHaveBook() {
        // Given
        String bookIsbn = "978-123456789";
        when(bookRepository.findBorrowedByUser(teacher.getId())).thenReturn(Collections.emptyList());

        // When
        boolean result = libraryService.returnBook(bookIsbn, teacher);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should get correct available copies count")
    void shouldGetCorrectAvailableCopiesCount() {
        // Given
        String bookIsbn = "978-123456789";
        when(bookRepository.getAvailableCopies(bookIsbn)).thenReturn(3);

        // When
        int count = libraryService.getAvailableCopies(bookIsbn);

        // Then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should get correct total copies count")
    void shouldGetCorrectTotalCopiesCount() {
        // Given
        String bookIsbn = "978-123456789";
        when(bookRepository.getTotalCopies(bookIsbn)).thenReturn(5);

        // When
        int count = libraryService.getTotalCopies(bookIsbn);

        // Then
        assertEquals(5, count);
    }

    @Test
    @DisplayName("Should check if book exists correctly")
    void shouldCheckIfBookExistsCorrectly() {
        // Given
        String bookIsbn = "978-123456789";
        when(bookRepository.existsByIsbn(bookIsbn)).thenReturn(true);

        // When
        boolean exists = libraryService.bookExists(bookIsbn);

        // Then
        assertTrue(exists);
    }
}