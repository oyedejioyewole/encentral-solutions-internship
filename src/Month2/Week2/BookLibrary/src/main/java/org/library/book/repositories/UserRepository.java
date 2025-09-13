package org.library.book.repositories;

import org.library.book.models.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UserRepository handles all data operations for users (e.g. for students and teachers)
 */
public class UserRepository {
    private final Map<String, User> users; // key = userId
    private final Map<UserType, List<User>> usersByType; // group by type for queries
    private static UserRepository instance;

    private UserRepository() {
        users = new HashMap<>();
        usersByType = new HashMap<>();
        usersByType.put(UserType.STUDENT, new ArrayList<>());
        usersByType.put(UserType.TEACHER, new ArrayList<>());
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    /**
     * Add a new user to the system
     */
    public void addUser(User user) {
        users.put(user.getId(), user);
        usersByType.get(user.getUserType()).add(user);
    }

    /**
     * Find user by ID
     */
    public User findById(String userId) {
        return users.get(userId);
    }

    /**
     * Find user by name (might be multiple with same name)
     */
    public List<User> findByName(String name) {
        return users.values().stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    /**
     * Get all students
     */
    public List<Student> findAllStudents() {
        return usersByType.get(UserType.STUDENT).stream()
                .map(user -> (Student) user)
                .collect(Collectors.toList());
    }

    /**
     * Get all teachers
     */
    public List<Teacher> findAllTeachers() {
        return usersByType.get(UserType.TEACHER).stream()
                .map(user -> (Teacher) user)
                .collect(Collectors.toList());
    }

    /**
     * Get senior students (grade 11-12)
     */
    public List<Student> findSeniorStudents() {
        return findAllStudents().stream()
                .filter(Student::isASenior)
                .collect(Collectors.toList());
    }

    /**
     * Get junior students (grade 9-10)
     */
    public List<Student> findJuniorStudents() {
        return findAllStudents().stream()
                .filter(student -> !student.isASenior())
                .collect(Collectors.toList());
    }

    /**
     * Check if user exists
     */
    public boolean existsById(String userId) {
        return users.containsKey(userId);
    }

    /**
     * Get all users
     */
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Remove user from system
     */
    public boolean removeUser(String userId) {
        User removedUser = users.remove(userId);
        if (removedUser != null) {
            usersByType.get(removedUser.getUserType()).remove(removedUser);
            return true;
        }
        return false;
    }

    /**
     * Update user information
     */
    public boolean updateUser(User updatedUser) {
        if (users.containsKey(updatedUser.getId())) {
            // Remove old version
            User oldUser = users.get(updatedUser.getId());
            usersByType.get(oldUser.getUserType()).remove(oldUser);

            // Add updated version
            users.put(updatedUser.getId(), updatedUser);
            usersByType.get(updatedUser.getUserType()).add(updatedUser);
            return true;
        }
        return false;
    }

    /**
     * Get user statistics
     */
    public Map<String, Integer> getUserStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Total Users", users.size());
        stats.put("Teachers", usersByType.get(UserType.TEACHER).size());
        stats.put("Students", usersByType.get(UserType.STUDENT).size());
        stats.put("Senior Students", findSeniorStudents().size());
        stats.put("Junior Students", findJuniorStudents().size());
        return stats;
    }
}