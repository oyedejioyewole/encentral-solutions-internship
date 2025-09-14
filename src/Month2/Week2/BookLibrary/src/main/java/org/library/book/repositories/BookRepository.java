package org.library.book.repositories;

import org.library.book.models.Book;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BookRepository handles all data operations for books
 */
public class BookRepository {
    private final Map<String, Book> books = new HashMap<>(); // { 'uniqueBookId': <Object: Book> }
    private final Map<String, List<Book>> booksWithTheirCopies = new HashMap<>(); // { 'bookIsbn': [ ..., <Object: Book> ] }
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
        booksWithTheirCopies.computeIfAbsent(book.getIsbn(), _ -> new ArrayList<>())
                .add(book);
    }

    public Map<String, List<Book>> searchBooksWithTitle(String query) {
        Map<String, List<Book>> matches = new HashMap<>();

        for (Map.Entry<String, List<Book>> book : booksWithTheirCopies.entrySet()) {
            try {
                Book firstBookCopy = book.getValue().getFirst();

                boolean hasQueryMatched = firstBookCopy.getTitle().toLowerCase().contains(query.toLowerCase());

                if (hasQueryMatched) {
                    matches.putIfAbsent(firstBookCopy.getIsbn(), book.getValue());
                }
            } catch (NoSuchElementException error) {
                System.out.println(error.getMessage());
            }
        }

        return matches;
    }

    /**
     * Find all copies of a book by title
     */
    public List<Book> findByIsbn(String isbn) {
        return booksWithTheirCopies.getOrDefault(isbn, new ArrayList<>());
    }

    /**
     * Find all AVAILABLE copies of a book by title
     * This is what LibraryService uses for borrowing
     */
    public List<Book> findAvailableByIsbn(String title) {
        return findByIsbn(title).stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * Get number of available copies for a book title
     */
    public int getAvailableCopies(String isbn) {
        return findAvailableByIsbn(isbn).size();
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
     * Get all books in the library (used in statistics)
     */
    public List<Book> findAll() {
        return books.values().stream().toList();
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
    public boolean existsByIsbn(String isbn) {
        return booksWithTheirCopies.containsKey(isbn) &&
                !booksWithTheirCopies.get(isbn).isEmpty();
    }

    public int getTotalCopies(String isbn) {
        return findByIsbn(isbn).size();
    }

    /**
     * Get summary of library inventory
     */
    public Map<String, Integer> getInventorySummary() {
        Map<String, Integer> summary = new HashMap<>();
        booksWithTheirCopies.forEach((isbn, copies) -> {
            Book firstCopy = copies.getFirst();
            long availableCount = copies.stream()
                    .mapToLong(book -> book.isAvailable() ? 1 : 0)
                    .sum();
            summary.put(String.format("%s (%s)", firstCopy.getTitle(), isbn), (int) availableCount / copies.size());
        });
        return summary;
    }
}