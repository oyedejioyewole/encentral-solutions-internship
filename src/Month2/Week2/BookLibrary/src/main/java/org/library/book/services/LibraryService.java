package org.library.book.services;

import org.library.book.models.Book;
import org.library.book.models.User;
import org.library.book.repositories.BookRepository;
import org.library.book.repositories.UserRepository;

import java.util.List;

/**
 * Basic LibraryService for CLI functionality
 * This is a simplified version without the priority queue for now
 */
public record LibraryService(BookRepository bookRepository, UserRepository userRepository) {
    /**
     * Borrow a book - basic implementation
     */
    public String borrowBook(String bookTitle, User user) {
        // Check if book exists
        if (!bookRepository.existsByTitle(bookTitle)) {
            return "Book not found: " + bookTitle;
        }

        // Find available copies
        List<Book> availableBooks = bookRepository.findAvailableByTitle(bookTitle);

        if (availableBooks.isEmpty()) {
            return "book taken";
        }

        // For now, just give the first available copy
        // TODO: Implement priority queue logic
        Book bookToBorrow = availableBooks.getFirst();
        bookToBorrow.borrowBook(user.getId());

        return "Book borrowed successfully: " + bookTitle + " (Copy " + bookToBorrow.getCopyNumber() + ")";
    }

    /**
     * Return a book
     */
    public boolean returnBook(String bookTitle, User user) {
        // Find the user's borrowed books
        List<Book> userBooks = bookRepository.findBorrowedByUser(user.getId());

        for (Book book : userBooks) {
            if (book.getTitle().equals(bookTitle)) {
                book.returnBook();
                return true;
            }
        }

        return false; // Book not found in user's borrowed books
    }

    /**
     * Get available copies count
     */
    public int getAvailableCopies(String bookTitle) {
        return bookRepository.getAvailableCopies(bookTitle);
    }

    /**
     * Check if book exists in library
     */
    public boolean bookExists(String bookTitle) {
        return bookRepository.existsByTitle(bookTitle);
    }
}