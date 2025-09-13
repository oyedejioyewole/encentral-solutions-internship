package org.library.book;

import org.library.book.extras.CLI;
import org.library.book.generators.*;
import org.library.book.models.*;
import org.library.book.repositories.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        BookRepository bookRepository = BookRepository.getInstance();
        UserRepository userRepository = UserRepository.getInstance();

        List<Book> registeredBooks = BookGenerator.generateRandomBooks(20);
        registeredBooks.forEach(bookRepository::addBook);

        List<Teacher> teachersRegisteredInLibrary = UserGenerator.generateRandomTeachers(5);
        teachersRegisteredInLibrary.forEach(userRepository::addUser);

        List<Student> studentsRegisteredInLibrary = UserGenerator.generateRandomStudents(5);
        studentsRegisteredInLibrary.forEach(userRepository::addUser);

        System.out.println("Registry for users and books have been loaded");

        CLI cli = new CLI();
        cli.start();
    }
}