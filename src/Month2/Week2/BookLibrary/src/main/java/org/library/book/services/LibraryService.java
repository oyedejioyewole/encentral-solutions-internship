package org.library.book.services;

import org.library.book.models.*;
import org.library.book.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main service class for library operations with priority-based borrowing
 * Implements comprehensive logging using Log4j2
 */
public class LibraryService {
    private static final Logger logger = LogManager.getLogger(LibraryService.class);

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PriorityQueueService priorityQueueService;

    // Track pending requests for each book title using concurrent collections for thread safety
    private final Map<String, PriorityQueue<BorrowRequest>> pendingRequests;

    public LibraryService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.priorityQueueService = new PriorityQueueService();
        this.pendingRequests = new ConcurrentHashMap<>();

        logger.info("LibraryService initialized successfully");
        logger.debug("Repository instances: BookRepository={}, UserRepository={}",
                bookRepository.getClass().getSimpleName(),
                userRepository.getClass().getSimpleName());
    }

    /**
     * Borrow a book with priority-based allocation
     * @param bookIsbn The title of the book to borrow
     * @param user The user requesting the book
     * @return Result message
     */
    public synchronized String borrowBook(String bookIsbn, User user) {
        logger.info("Borrow request initiated - User: {}, Book: '{}'", user.getName(), bookIsbn);

        // Validate inputs
        if (bookIsbn == null || bookIsbn.trim().isEmpty()) {
            logger.warn("Invalid book title provided - User: {}", user != null ? user.getName() : "null");
            return "Invalid book title";
        }
        if (user == null) {
            logger.warn("Null user provided for book: '{}'", bookIsbn);
            return "Invalid user";
        }

        logger.debug("Processing borrow request - User: {} (Priority: {}), Book: '{}'",
                user.getName(), user.getPriorityLevel(), bookIsbn);

        // Check if book exists in library
        if (!bookRepository.existsByIsbn(bookIsbn)) {
            logger.warn("Book not found in library - Title: '{}', User: {}", bookIsbn, user.getName());
            return "Book not found: " + bookIsbn;
        }

        // Check if user already has this book
        List<Book> userBooks = bookRepository.findBorrowedByUser(user.getId());
        boolean alreadyHasBook = userBooks.stream()
                .anyMatch(book -> book.getTitle().equals(bookIsbn));

        if (alreadyHasBook) {
            logger.warn("User already has this book - User: {}, Book: '{}'", user.getName(), bookIsbn);
            return "You already have a copy of this book";
        }

        // Check for available copies
        List<Book> availableBooks = bookRepository.findAvailableByIsbn(bookIsbn);

        if (!availableBooks.isEmpty()) {
            // Book is available - borrow immediately
            Book bookToBorrow = availableBooks.getFirst();
            bookToBorrow.borrowBook(user.getId());

            logger.info("Book borrowed successfully - User: {}, Book: '{}', Copy: {}",
                    user.getName(), bookIsbn, bookToBorrow.getCopyNumber());

            // Process any pending requests for this book
            processPendingRequests(bookIsbn);

            return "Book borrowed successfully: " + bookIsbn + " (Copy " + bookToBorrow.getCopyNumber() + ")";
        }

        // No copies available - add to priority queue
        logger.info("No copies available, adding to priority queue - User: {}, Book: '{}'",
                user.getName(), bookIsbn);
        return handleBookNotAvailable(bookIsbn, user);
    }

    /**
     * Handle case when book is not available - add to priority queue
     */
    private String handleBookNotAvailable(String bookTitle, User user) {
        // Create borrow request
        String requestId = generateRequestId();
        BorrowRequest request = new BorrowRequest(requestId, user, bookTitle);

        logger.debug("Creating borrow request - ID: {}, User: {}, Book: '{}'",
                requestId, user.getName(), bookTitle);

        // Add to priority queue for this book
        PriorityQueue<BorrowRequest> bookQueue = pendingRequests.computeIfAbsent(
                bookTitle,
                k -> {
                    logger.debug("Creating new priority queue for book: '{}'", bookTitle);
                    return priorityQueueService.createPriorityQueue();
                }
        );

        // Check if user already has a pending request for this book
        boolean alreadyInQueue = bookQueue.stream()
                .anyMatch(req -> req.getUser().getId().equals(user.getId()));

        if (alreadyInQueue) {
            logger.warn("User already has pending request for book - User: {}, Book: '{}'",
                    user.getName(), bookTitle);
            return "You already have a pending request for this book";
        }

        bookQueue.offer(request);

        // Calculate position in queue
        int position = calculateQueuePosition(bookQueue, user);

        logger.info("User added to priority queue - User: {}, Book: '{}', Position: {}, Priority: {}",
                user.getName(), bookTitle, position, user.getPriorityLevel());

        return "book taken - You are #" + position + " in the priority queue";
    }

    /**
     * Return a book and process pending requests
     * @param bookTitle The title of the book to return
     * @param user The user returning the book
     * @return true if successful, false otherwise
     */
    public synchronized boolean returnBook(String bookTitle, User user) {
        logger.info("Return request initiated - User: {}, Book: '{}'", user.getName(), bookTitle);

        // Find the user's borrowed books
        List<Book> userBooks = bookRepository.findBorrowedByUser(user.getId());

        Book bookToReturn = null;
        for (Book book : userBooks) {
            if (book.getTitle().equals(bookTitle)) {
                bookToReturn = book;
                break;
            }
        }

        if (bookToReturn == null) {
            logger.warn("User does not have this book to return - User: {}, Book: '{}'",
                    user.getName(), bookTitle);
            return false;
        }

        // Return the book
        bookToReturn.returnBook();

        logger.info("Book returned successfully - User: {}, Book: '{}', Copy: {}",
                user.getName(), bookTitle, bookToReturn.getCopyNumber());

        // Process pending requests for this book
        processPendingRequests(bookTitle);

        return true;
    }

    /**
     * Process pending requests when a book becomes available
     */
    private void processPendingRequests(String bookIsbn) {
        PriorityQueue<BorrowRequest> bookQueue = pendingRequests.get(bookIsbn);

        if (bookQueue == null || bookQueue.isEmpty()) {
            logger.debug("No pending requests to process for book: '{}'", bookIsbn);
            return;
        }

        logger.info("Processing pending requests for book: '{}', Queue size: {}", bookIsbn, bookQueue.size());

        // Check for available copies
        List<Book> availableBooks = bookRepository.findAvailableByIsbn(bookIsbn);

        int assignmentCount = 0;
        while (!availableBooks.isEmpty() && !bookQueue.isEmpty()) {
            // Get highest priority request
            BorrowRequest nextRequest = bookQueue.poll();

            logger.debug("Processing next request from queue - User: {}, Priority: {}",
                    nextRequest.getUser().getName(), nextRequest.getUser().getPriorityLevel());

            // Verify user still exists and is valid
            User user = userRepository.findById(nextRequest.getUser().getId());
            if (user == null) {
                logger.warn("Invalid user in queue, skipping - User ID: {}", nextRequest.getUser().getId());
                continue;
            }

            // Check if user already has this book (might have gotten it elsewhere)
            List<Book> userBooks = bookRepository.findBorrowedByUser(user.getId());
            boolean alreadyHasBook = userBooks.stream()
                    .anyMatch(book -> book.getTitle().equals(bookIsbn));

            if (alreadyHasBook) {
                logger.info("User already has book, skipping queue assignment - User: {}, Book: '{}'",
                        user.getName(), bookIsbn);
                continue;
            }

            // Assign book to user
            Book bookToAssign = availableBooks.getFirst();
            bookToAssign.borrowBook(user.getId());
            assignmentCount++;

            // Update available books list
            availableBooks = bookRepository.findAvailableByIsbn(bookIsbn);

            logger.info("Auto-assigned book from queue - User: {}, Book: '{}', Copy: {}",
                    user.getName(), bookIsbn, bookToAssign.getCopyNumber());
        }

        // Clean up empty queue
        if (bookQueue.isEmpty()) {
            pendingRequests.remove(bookIsbn);
            logger.debug("Removed empty queue for book: '{}'", bookIsbn);
        }

        if (assignmentCount > 0) {
            logger.info("Completed queue processing - Book: '{}', Assignments made: {}",
                    bookIsbn, assignmentCount);
        }
    }

    /**
     * Get available copies count for a book
     */
    public int getAvailableCopies(String bookIsbn) {
        int count = bookRepository.getAvailableCopies(bookIsbn);
        logger.debug("Available copies query - Book: '{}', Count: {}", bookIsbn, count);
        return count;
    }

    /**
     * Get total copies count for a book
     */
    public int getTotalCopies(String bookIsbn) {
        int count = bookRepository.getTotalCopies(bookIsbn);
        logger.debug("Total copies query - Book: '{}', Count: {}", bookIsbn, count);
        return count;
    }

    /**
     * Check if book exists in library
     */
    public boolean bookExists(String bookIsbn) {
        boolean exists = bookRepository.existsByIsbn(bookIsbn);
        logger.debug("Book existence query - Book: '{}', Exists: {}", bookIsbn, exists);
        return exists;
    }

    /**
     * Get all books borrowed by a user
     */
    public List<Book> getUserBorrowedBooks(String userId) {
        List<Book> books = bookRepository.findBorrowedByUser(userId);
        logger.debug("User borrowed books query - User ID: {}, Count: {}", userId, books.size());
        return books;
    }

    /**
     * Get pending requests for a book
     */
    public List<BorrowRequest> getPendingRequests(String bookTitle) {
        PriorityQueue<BorrowRequest> bookQueue = pendingRequests.get(bookTitle);
        if (bookQueue == null) {
            logger.debug("No pending requests for book: '{}'", bookTitle);
            return new ArrayList<>();
        }

        List<BorrowRequest> requests = new ArrayList<>(bookQueue);
        logger.debug("Pending requests query - Book: '{}', Count: {}", bookTitle, requests.size());
        return requests;
    }

    /**
     * Get user's position in queue for a book
     */
    public int getUserQueuePosition(String bookTitle, User user) {
        PriorityQueue<BorrowRequest> bookQueue = pendingRequests.get(bookTitle);
        if (bookQueue == null) {
            logger.debug("No queue exists for book: '{}', User: {}", bookTitle, user.getName());
            return -1;
        }

        int position = calculateQueuePosition(bookQueue, user);
        logger.debug("User queue position - User: {}, Book: '{}', Position: {}",
                user.getName(), bookTitle, position);
        return position;
    }

    /**
     * Cancel a user's pending request for a book
     */
    public boolean cancelBorrowRequest(String bookTitle, User user) {
        PriorityQueue<BorrowRequest> bookQueue = pendingRequests.get(bookTitle);
        if (bookQueue == null) {
            logger.debug("No queue to cancel from - Book: '{}', User: {}", bookTitle, user.getName());
            return false;
        }

        BorrowRequest toRemove = null;
        for (BorrowRequest request : bookQueue) {
            if (request.getUser().getId().equals(user.getId())) {
                toRemove = request;
                break;
            }
        }

        if (toRemove != null) {
            bookQueue.remove(toRemove);

            logger.info("Borrow request cancelled - User: {}, Book: '{}'", user.getName(), bookTitle);

            // Clean up empty queue
            if (bookQueue.isEmpty()) {
                pendingRequests.remove(bookTitle);
                logger.debug("Removed empty queue after cancellation - Book: '{}'", bookTitle);
            }

            return true;
        }

        logger.debug("No request found to cancel - User: {}, Book: '{}'", user.getName(), bookTitle);
        return false;
    }

    // Helper methods
    private String generateRequestId() {
        return "REQ-" + System.currentTimeMillis() + "-" +
                (int)(Math.random() * 1000);
    }

    private int calculateQueuePosition(PriorityQueue<BorrowRequest> queue, User user) {
        List<BorrowRequest> queueList = new ArrayList<>(queue);
        queueList.sort(priorityQueueService.getBorrowRequestComparator());

        for (int index = 0; index < queueList.size(); index++) {
            if (queueList.get(index).getUser().getId().equals(user.getId())) {
                return index + 1; // Position starts from 1
            }
        }

        return -1; // User not found in queue
    }
}