package org.library.book.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {
    private Book book;

    @BeforeEach
    void setUp() {
        // Create a fresh book before each test
        book = new Book("978-123456789", "Baby steps into the human psychic", "John May-weather", 1);
    }

    @Test
    @DisplayName("Should successfully borrow an available book")
    void shouldBorrowAvailableBook() {
        // Given - book is available (set up in @BeforeEach)
        String userId = "mary-beckham:final-year";

        // When
        book.borrowBook(userId);

        // Then
        assertFalse(book.isAvailable(), "Book should no longer be available");
        assertEquals(userId, book.getBorrowedBy(), "Book should be borrowed by the user");
        assertNotNull(book.getBorrowedAt(), "Borrowed date should be set");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when borrowing already borrowed book")
    void shouldThrowExceptionWhenBorrowingAlreadyBorrowedBook() {
        // Given - book is already borrowed
        String firstUser = "mary-beckham:final-year";
        String secondUser = "christopher-gerald:2nd-year";
        book.borrowBook(firstUser); // First user borrows the book

        // When & Then - Second user tries to borrow the same book
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> book.borrowBook(secondUser),
            "Should throw IllegalStateException when trying to borrow already borrowed book"
        );

        // Verify the exception message
        assertEquals("Book is already borrowed", exception.getMessage());

        // Verify book state hasn't changed
        assertFalse(book.isAvailable(), "Book should still be unavailable");
        assertEquals(firstUser, book.getBorrowedBy(), "Book should still be borrowed by first user");
    }

    @Test
    @DisplayName("Should successfully return a borrowed book")
    void shouldReturnBorrowedBook() {
        // Given - book is borrowed
        String userId = "USER001";
        book.borrowBook(userId);

        // When
        book.returnBook();

        // Then
        assertTrue(book.isAvailable(), "Book should be available after return");
        assertNull(book.getBorrowedBy(), "BorrowedBy should be null after return");
        assertNull(book.getBorrowedAt(), "Borrowed date should be null after return");
    }

    @Test
    @DisplayName("Should handle returning an already available book gracefully")
    void shouldHandleReturningAvailableBook() {
        // Given - book is available (not borrowed)
        assertTrue(book.isAvailable(), "Book should start as available");

        // When - return an already available book
        assertDoesNotThrow(() -> book.returnBook(),
                "Returning an available book should not throw exception");

        // Then - book should still be available
        assertTrue(book.isAvailable(), "Book should remain available");
        assertNull(book.getBorrowedBy(), "BorrowedBy should remain null");
        assertNull(book.getBorrowedAt(), "Borrowed date should remain null");
    }

    @Test
    @DisplayName("Should create unique ID correctly")
    void shouldCreateCorrectUniqueId() {
        // When
        String uniqueId = book.getUniqueId();

        // Then
        assertEquals("978-123456789-1", uniqueId, "Unique ID should combine ISBN and copy number");
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void shouldImplementEqualsAndHashCode() {
        // Given
        Book sameBook = new Book("978-123456789", "Different Title", "Different Author", 1);
        Book differentCopy = new Book("978-123456789", "Java Programming", "John Author", 2);
        Book differentIsbn = new Book("978-987654321", "Java Programming", "John Author", 1);

        // Then - same ISBN and copy number should be equal
        assertEquals(book, sameBook, "Books with same ISBN and copy number should be equal");
        assertEquals(book.hashCode(), sameBook.hashCode(), "Equal books should have same hash code");

        // Different copy number should not be equal
        assertNotEquals(book, differentCopy, "Books with different copy numbers should not be equal");

        // Different ISBN should not be equal
        assertNotEquals(book, differentIsbn, "Books with different ISBNs should not be equal");
    }

    @Test
    @DisplayName("Should handle null userId when borrowing")
    void shouldHandleNullUserIdWhenBorrowing() {
        // When & Then
        assertDoesNotThrow(() -> book.borrowBook(null),
                "Should not throw exception for null userId");

        assertFalse(book.isAvailable(), "Book should be marked as borrowed");
        assertNull(book.getBorrowedBy(), "BorrowedBy should be null");
    }

    @Test
    @DisplayName("Should handle empty string userId when borrowing")
    void shouldHandleEmptyUserIdWhenBorrowing() {
        // When
        book.borrowBook("");

        // Then
        assertFalse(book.isAvailable(), "Book should be marked as borrowed");
        assertEquals("", book.getBorrowedBy(), "BorrowedBy should be empty string");
        assertNotNull(book.getBorrowedAt(), "Borrowed date should still be set");
    }

    @Test
    @DisplayName("Should maintain borrowed date when exception is thrown")
    void shouldMaintainStateWhenExceptionThrown() {
        // Given - book is borrowed by first user
        String firstUser = "USER001";
        book.borrowBook(firstUser);
        var originalBorrowDate = book.getBorrowedAt();

        // When - second user tries to borrow (will throw exception)
        assertThrows(IllegalStateException.class, () -> book.borrowBook("USER002"));

        // Then - original state should be preserved
        assertEquals(firstUser, book.getBorrowedBy(), "Original borrower should remain unchanged");
        assertEquals(originalBorrowDate, book.getBorrowedAt(), "Original borrow date should remain unchanged");
        assertFalse(book.isAvailable(), "Book should still be unavailable");
    }
}

// Additional test class for testing multiple scenarios
class BookIntegrationTest {
    @Test
    @DisplayName("Should handle complete borrow-return cycle correctly")
    void shouldHandleCompleteBorrowReturnCycle() {
        // Given
        Book book = new Book("978-123456789", "Test Book", "Test Author", 1);
        String userId = "USER001";

        // Initial state
        assertTrue(book.isAvailable());
        assertNull(book.getBorrowedBy());
        assertNull(book.getBorrowedAt());

        // Borrow
        book.borrowBook(userId);
        assertFalse(book.isAvailable());
        assertEquals(userId, book.getBorrowedBy());
        assertNotNull(book.getBorrowedAt());

        // Return
        book.returnBook();
        assertTrue(book.isAvailable());
        assertNull(book.getBorrowedBy());
        assertNull(book.getBorrowedAt());

        // Should be able to borrow again
        assertDoesNotThrow(() -> book.borrowBook("USER002"));
    }
}