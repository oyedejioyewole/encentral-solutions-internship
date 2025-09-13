package org.library.book.repositories;

import org.library.book.models.Book;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BookRepository handles all data operations for books
 */
public class BookRepository {
    private final Map<String, Book> books = new HashMap<>(); // { 'uniqueBookId': <Object: Book> }
    private final Map<String, List<Book>> booksByTitle = new HashMap<>(); // { 'bookTitle': [ ..., <Object: Book> ] }
    private static BookRepository instance;

    private BookRepository() {}

    public static BookRepository getInstance() {
        if (instance == null) {
            instance = new BookRepository();
        }

        return instance;
    }

    /**
     * Add a new book copy to the library
     */
    public void addBook(Book book) {
        // Store by unique ID for fast individual lookup
        books.put(book.getUniqueId(), book);

        // Group by title for finding all copies of same book
        booksByTitle.computeIfAbsent(book.getTitle(), _ -> new ArrayList<>())
                .add(book);
    }

    /**
     * Find a specific book copy by ISBN and copy number
     */
    public Book findByUniqueId(String isbn, int copyNumber) {
        String uniqueId = isbn + "-" + copyNumber;
        return books.get(uniqueId);
    }

    public List<Book> searchBooksWithTitle(String query) {
        List<Book> matches = new ArrayList<>();

        for (Book book : books.values()) {

            if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                matches.add(book);
            };
        }

        return matches;
    }

    /**
     * Find all copies of a book by title
     */
    public List<Book> findByTitle(String title) {
        return booksByTitle.getOrDefault(title, new ArrayList<>());
    }

    /**
     * Find all AVAILABLE copies of a book by title
     * This is what LibraryService uses for borrowing
     */
    public List<Book> findAvailableByTitle(String title) {
        return findByTitle(title).stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * Get total number of copies for a book title
     */
    public int getTotalCopies(String title) {
        return findByTitle(title).size();
    }

    /**
     * Get number of available copies for a book title
     */
    public int getAvailableCopies(String title) {
        return findAvailableByTitle(title).size();
    }

    /**
     * Find all books borrowed by a specific user
     */
    public List<Book> findBorrowedByUser(String userId) {
        return books.values().stream()
                .filter(book -> !book.isAvailable() &&
                        userId.equals(book.getBorrowedBy()))
                .collect(Collectors.toList());
    }

    /**
     * Get all books in the library (for admin/reports)
     */
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    /**
     * Get all available books in the library
     */
    public List<Book> findAllAvailable() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * Check if any copy of a book exists in the library
     */
    public boolean existsByTitle(String title) {
        return booksByTitle.containsKey(title) &&
                !booksByTitle.get(title).isEmpty();
    }

    /**
     * Get summary of library inventory
     */
    public Map<String, Integer> getInventorySummary() {
        Map<String, Integer> summary = new HashMap<>();
        booksByTitle.forEach((title, copies) -> {
            long availableCount = copies.stream()
                    .mapToLong(book -> book.isAvailable() ? 1 : 0)
                    .sum();
            summary.put(title, (int) availableCount / copies.size());
        });
        return summary;
    }
}