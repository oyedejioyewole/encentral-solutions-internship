package org.library.book.generators;

import org.library.book.models.Book;

import java.util.*;

public class BookGenerator {
    private static final String[] BOOK_TITLES = {
        "Java: The Complete Reference",
        "Clean Code",
        "Design Patterns",
        "Spring Boot in Action",
        "Effective Java",
        "Head First Design Patterns",
        "Java Concurrency in Practice",
        "Spring Framework Essentials",
        "Data Structures and Algorithms",
        "Introduction to Programming",
        "Web Development with HTML & CSS",
        "JavaScript: The Good Parts",
        "Python Programming",
        "Database Management Systems",
        "Computer Networks",
        "Operating System Concepts",
        "Software Engineering",
        "Machine Learning Basics",
        "Artificial Intelligence",
        "Cybersecurity Fundamentals",
        "Mobile App Development",
        "Cloud Computing",
        "Mathematics for Computer Science",
        "Statistics and Probability",
        "Linear Algebra"
    };

    private static final String[] AUTHORS = {
        "Herbert Schildt",
        "Robert Martin",
        "Gang of Four",
        "Craig Walls",
        "Joshua Bloch",
        "Eric Freeman",
        "Brian Goetz",
        "Rod Johnson",
        "Thomas Cormen",
        "John Smith",
        "Jane Doe",
        "David Johnson",
        "Sarah Wilson",
        "Michael Brown",
        "Emily Davis",
        "Chris Anderson",
        "Lisa Thompson",
        "Mark Garcia",
        "Anna Martinez",
        "James Taylor",
        "Maria Rodriguez",
        "Kevin Lee",
        "Rachel White",
        "Daniel Harris",
        "Jennifer Clark"
    };

    private static final Random random = new Random();

    public static List<Book> generateRandomBooks(int numberOfBooks) {
        List<Book> books = new ArrayList<>();
        Set<String> usedTitles = new HashSet<>();

        for (int i = 0; i < numberOfBooks; i++) {
            String title;
            do {
                title = BOOK_TITLES[random.nextInt(BOOK_TITLES.length)];
            } while (usedTitles.contains(title));

            usedTitles.add(title);
            books.add(new Book(generateISBN(), title, AUTHORS[random.nextInt(AUTHORS.length)], random.nextInt(10)));
        }

        return books;
    }

    private static String generateISBN() {
        // Generate a simple ISBN-like number
        return "978-" +
            (random.nextInt(900) + 100) + "-" +  // 3 digits
            (random.nextInt(900) + 100) + "-" +  // 3 digits
            (random.nextInt(90) + 10);           // 2 digits
    }
}
