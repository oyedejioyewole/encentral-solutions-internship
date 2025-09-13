package org.library.book.extras;

import org.library.book.models.*;
import org.library.book.repositories.*;
import org.library.book.services.LibraryService;

import java.util.*;

/**
 * Command Line Interface for the Library Management System
 */
public class CLI {
    private final Scanner scanner;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LibraryService libraryService;
    private boolean running;

    public CLI() {
        this.scanner = new Scanner(System.in);
        this.bookRepository = BookRepository.getInstance();
        this.userRepository = UserRepository.getInstance();
        this.libraryService = new LibraryService(bookRepository, userRepository);
        this.running = true;
    }

    /**
     * Main entry point for the CLI
     */
    public void start() {
        printWelcomeMessage();

        while (running) {
            try {
                displayMainMenu();
                int choice = getUserChoice();
                handleMainMenuChoice(choice);
            } catch (Exception e) {
                System.out.println("❌ An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }

        System.out.println("👋 Thank you for using the Library Management System!");
        scanner.close();
    }

    private void printWelcomeMessage() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║        📚    LIBRARY MANAGEMENT SYSTEM     📚    ║");
        System.out.println("║                                              ║");
        System.out.println("║      Welcome to the Book Borrowing System    ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
    }

    private void displayMainMenu() {
        System.out.println("═══════════════ MAIN MENU ═══════════════");
        System.out.println("1. 📖 Browse Books");
        System.out.println("2. 📝 Borrow a Book");
        System.out.println("3. 📤 Return a Book");
        System.out.println("4. 📊 View library stats");
        System.out.println("0. 🚪 Exit");
        System.out.println("═════════════════════════════════════════");
        System.out.print("Choose an option (0-4): ");
    }

    private int getUserChoice() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            return choice;
        } catch (InputMismatchException e) {
            scanner.nextLine(); // clear invalid input
            System.out.println("❌ Please enter a valid number!");
            return -1;
        }
    }

    private void handleMainMenuChoice(int choice) {
        System.out.println();

        switch (choice) {
            case 1:
                handleBrowseBooks();
                break;
            case 2:
                handleBorrowBook();
                break;
            case 3:
                handleReturnBook();
                break;
            case 4:
                handleViewReports();
                break;
            case 0:
                running = false;
                break;
            default:
                System.out.println("❌ Invalid choice! Please try again.");
        }

        if (running && choice != 4) {
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    // ==================== BOOK BROWSING ====================

    private void handleBrowseBooks() {
        System.out.println("📖 BROWSE BOOKS");
        System.out.println("1. View All Available Books");
        System.out.println("2. Search Books by Title");
        System.out.println("3. View Book Details");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose an option: ");

        int choice = getUserChoice();
        System.out.println();

        switch (choice) {
            case 1:
                displayAllAvailableBooks();
                break;
            case 2:
                searchBooksByTitle();
                break;
            case 3:
                viewBookDetails();
                break;
            case 0:
                return;
            default:
                System.out.println("❌ Invalid choice!");
        }
    }

    private void displayAllAvailableBooks() {
        List<Book> availableBooks = bookRepository.findAllAvailable();

        if (availableBooks.isEmpty()) {
            System.out.println("📚 No books are currently available.");
            return;
        }

        System.out.format("There are currently %d books available to rent%n", availableBooks.size());
        System.out.println("─────────────────────────────────────────────────────────");

        // Group by title for better display
        Map<String, List<Book>> booksByTitle = new HashMap<>();
        for (Book book : availableBooks) {
            booksByTitle.computeIfAbsent(book.getTitle(), k -> new ArrayList<>()).add(book);
        }

        booksByTitle.forEach((title, copies) -> {
            Book firstCopy = copies.getFirst();
            System.out.printf("📖 %-40s | By: %-20s | Copies: %d\n",
                    title, firstCopy.getAuthor(), copies.size());
        });
    }

    private void searchBooksByTitle() {
        System.out.print("🔍 Enter book title to search: ");
        String searchTerm = scanner.nextLine().trim();

        if (searchTerm.isEmpty()) {
            System.out.println("❌ Please enter a search term.");
            return;
        }

        List<Book> matchingBooks = bookRepository.searchBooksWithTitle(searchTerm);

        if (matchingBooks.isEmpty()) {
            System.out.println("❌ No books found matching: " + searchTerm);
            return;
        }

        System.out.format("🔍 SEARCH RESULTS for '%s'%n", searchTerm);
        System.out.println("─────────────────────────────────────────────────────────");

        Map<String, List<Book>> booksByTitle = new HashMap<>();
        for (Book book : matchingBooks) {
            booksByTitle.computeIfAbsent(book.getTitle(), k -> new ArrayList<>()).add(book);
        }

        booksByTitle.forEach((title, copies) -> {
            Book firstCopy = copies.getFirst();
            long availableCount = copies.stream().mapToLong(book -> book.isAvailable() ? 1 : 0).sum();
            System.out.printf("📖 %-35s | By: %-15s | Available: %d/%d\n",
                    title, firstCopy.getAuthor(), availableCount, copies.size());
        });
    }

    private void viewBookDetails() {
        System.out.print("📖 Enter exact book title: ");
        String title = scanner.nextLine().trim();

        List<Book> bookCopies = bookRepository.findByTitle(title);
        if (bookCopies.isEmpty()) {
            System.out.println("❌ Book not found: " + title);
            return;
        }

        Book firstBook = bookCopies.getFirst();
        System.out.format("""
         BOOK DETAILS:
        ─────────────────────────────────────────
        Title: %s
        Author: %s
        ISBN: %s
        Total copies: %d
        """, firstBook.getTitle(), firstBook.getAuthor(), firstBook.getIsbn(), bookCopies.size());

        long availableCount = bookCopies.stream().mapToLong(book -> book.isAvailable() ? 1 : 0).sum();
        System.out.println("Available Copies: " + availableCount);

        if (availableCount < bookCopies.size()) {
            System.out.println("\n📝 BORROWED COPIES:");
            for (Book book : bookCopies) {
                if (!book.isAvailable()) {
                    System.out.println("  Copy " + book.getCopyNumber() + " - Borrowed by: " + book.getBorrowedBy());
                }
            }
        }
    }

    // ==================== BORROWING & RETURNING ====================

    private void handleBorrowBook() {
        System.out.println("📝 BORROW A BOOK");
        System.out.print("👤 Enter your User ID: ");
        String userId = scanner.nextLine().trim();

        User user = userRepository.findById(userId);
        if (user == null) {
            System.out.println("❌ User not found: " + userId);
            return;
        }

        System.out.print("📖 Enter book title: ");
        String bookTitle = scanner.nextLine().trim();

        if (bookTitle.isEmpty()) {
            System.out.println("❌ Book title cannot be empty!");
            return;
        }

        // Show user their priority level
        String priorityLevel = getPriorityDescription(user);
        System.out.println("🎯 Your priority level: " + priorityLevel);

        // Attempt to borrow
        String result = libraryService.borrowBook(bookTitle, user);

        if (result.equals("book taken")) {
            System.out.println("❌ " + result);
            System.out.println("💡 All copies of '" + bookTitle + "' are currently borrowed.");
        } else if (result.startsWith("Book not found")) {
            System.out.println("❌ " + result);
            System.out.println("💡 Try browsing available books first.");
        } else {
            System.out.println("✅ " + result);
        }
    }

    private void handleReturnBook() {
        System.out.println("📤 RETURN A BOOK");
        System.out.print("👤 Enter your User ID: ");
        String userId = scanner.nextLine().trim();

        User user = userRepository.findById(userId);
        if (user == null) {
            System.out.println("❌ User not found: " + userId);
            return;
        }

        // Show user's borrowed books
        List<Book> borrowedBooks = bookRepository.findBorrowedByUser(userId);
        if (borrowedBooks.isEmpty()) {
            System.out.println("ℹ️  You have no books to return.");
            return;
        }

        System.out.println("📚 YOUR BORROWED BOOKS:");
        for (int i = 0; i < borrowedBooks.size(); i++) {
            Book book = borrowedBooks.get(i);
            System.out.printf("%d. %s (Copy %d)\n",
                    i + 1, book.getTitle(), book.getCopyNumber());
        }

        System.out.print("Choose book to return (1-" + borrowedBooks.size() + "): ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice < 1 || choice > borrowedBooks.size()) {
                System.out.println("❌ Invalid choice!");
                return;
            }

            Book bookToReturn = borrowedBooks.get(choice - 1);
            boolean success = libraryService.returnBook(bookToReturn.getTitle(), user);

            if (success) {
                System.out.println("✅ Book returned successfully: " + bookToReturn.getTitle());
            } else {
                System.out.println("❌ Failed to return book. Please try again.");
            }

        } catch (InputMismatchException e) {
            scanner.nextLine(); // clear invalid input
            System.out.println("❌ Please enter a valid number!");
        }
    }

    // ==================== REPORTS ====================

    private void handleViewReports() {
        System.out.println("📊 REPORTS");
        System.out.println("1. Library Statistics");
        System.out.println("2. User Statistics");
        System.out.println("3. Popular Books");
        System.out.println("4. Currently Borrowed Books");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose a report: ");

        int choice = getUserChoice();
        System.out.println();

        switch (choice) {
            case 1:
                showLibraryStatistics();
                break;
            case 2:
                showUserStatistics();
                break;
            case 3:
                showPopularBooks();
                break;
            case 4:
                showCurrentlyBorrowedBooks();
                break;
            case 0:
                return;
            default:
                System.out.println("❌ Invalid choice!");
        }
    }

    private void showLibraryStatistics() {
        List<Book> allBooks = bookRepository.findAll();
        List<Book> availableBooks = bookRepository.findAllAvailable();

        System.out.println("📊 LIBRARY STATISTICS");
        System.out.println("─────────────────────────────────");
        System.out.println("Total Books: " + allBooks.size());
        System.out.println("Available Books: " + availableBooks.size());
        System.out.println("Borrowed Books: " + (allBooks.size() - availableBooks.size()));

        // Calculate borrowing rate
        double borrowingRate = allBooks.isEmpty() ? 0 :
                ((double)(allBooks.size() - availableBooks.size()) / allBooks.size()) * 100;
        System.out.printf("Borrowing Rate: %.1f%%\n", borrowingRate);

        // Show inventory summary
        System.out.println("\n📚 INVENTORY BY TITLE:");
        Map<String, Integer> inventory = bookRepository.getInventorySummary();
        inventory.forEach((title, availability) -> {
            System.out.println("  " + title + ": " + availability);
        });
    }

    private void showUserStatistics() {
        Map<String, Integer> userStats = userRepository.getUserStats();

        System.out.println("👥 USER STATISTICS");
        System.out.println("─────────────────────────────────");
        userStats.forEach((category, count) -> {
            System.out.println(category + ": " + count);
        });
    }

    private void showPopularBooks() {
        List<Book> allBooks = bookRepository.findAll();
        Map<String, Integer> borrowCounts = new HashMap<>();

        // Count borrowed books by title
        for (Book book : allBooks) {
            if (!book.isAvailable()) {
                borrowCounts.merge(book.getTitle(), 1, Integer::sum);
            }
        }

        if (borrowCounts.isEmpty()) {
            System.out.println("📚 No books are currently borrowed.");
            return;
        }

        System.out.println("🔥 POPULAR BOOKS (Currently Borrowed)");
        System.out.println("─────────────────────────────────────────");

        borrowCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    System.out.println("📖 " + entry.getKey() + ": " + entry.getValue() + " copies borrowed");
                });
    }

    private void showCurrentlyBorrowedBooks() {
        List<Book> allBooks = bookRepository.findAll();
        List<Book> borrowedBooks = allBooks.stream()
                .filter(book -> !book.isAvailable())
                .sorted((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()))
                .toList();

        if (borrowedBooks.isEmpty()) {
            System.out.println("📚 No books are currently borrowed.");
            return;
        }

        System.out.println("📋 CURRENTLY BORROWED BOOKS (" + borrowedBooks.size() + " total)");
        System.out.println("─────────────────────────────────────────────────────────────");

        for (Book book : borrowedBooks) {
            User borrower = userRepository.findById(book.getBorrowedBy());
            String borrowerInfo = borrower != null ? borrower.getName() : "Unknown User";

            System.out.printf("📖 %-30s | Copy %d | Borrowed by: %-20s | Date: %s\n",
                    book.getTitle(), book.getCopyNumber(),
                    borrowerInfo, book.getBorrowedAt().toLocalDate());
        }
    }

    private String getPriorityDescription(User user) {
        int priority = user.getPriorityLevel();
        return switch (priority) {
            case 1 -> "Highest (Teacher)";
            case 2 -> "Medium (Senior Student)";
            case 3 -> "Lower (Junior Student)";
            default -> "Unknown";
        };
    }
}