package org.library.book.generators;

import org.library.book.models.Student;
import org.library.book.models.Teacher;

import java.util.*;

public class UserGenerator {
    private static final String[] FIRST_NAMES = {
            "Alice", "Bob", "Carol", "David", "Emma", "Frank", "Grace", "Henry",
            "Ivy", "Jack", "Kate", "Liam", "Maya", "Noah", "Olivia", "Peter",
            "Quinn", "Rachel", "Sam", "Tara", "Uma", "Victor", "Wendy", "Xavier",
            "Yara", "Zoe", "Alex", "Blake", "Casey", "Drew", "Eli", "Finn",
            "Gia", "Hunter", "Ian", "Jade", "Kai", "Luna", "Max", "Nora"
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
            "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
            "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
            "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark",
            "Ramirez", "Lewis", "Robinson", "Walker", "Young", "Allen", "King",
            "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores", "Green",
            "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell"
    };

    private static final String[] TEACHER_DEPARTMENTS = {
            "Mathematics", "Computer Science", "Physics", "Chemistry", "Biology",
            "English", "History", "Geography", "Economics", "Psychology",
            "Philosophy", "Art", "Music", "Physical Education", "Foreign Languages",
            "Engineering", "Statistics", "Environmental Science", "Political Science",
            "Sociology", "Literature", "Creative Writing", "Drama", "Business Studies"
    };

    private static final Random random = new Random();

    private static int teacherIdCounter = 1;
    private static int studentIdCounter = 1;

    private static Teacher generateRandomTeacher() {
        String id = "T" + String.format("%03d", teacherIdCounter++);
        String name = generateRandomName();
        String department = TEACHER_DEPARTMENTS[random.nextInt(TEACHER_DEPARTMENTS.length)];

        return new Teacher(id, name, department);
    }

    private static Student generateRandomStudent() {
        final int studentLevelDeterminer = random.nextInt(2) + 1;
        String studentId = String.format("%s%03d", studentLevelDeterminer == 1 ? "JS" : "SS" , studentIdCounter++);
        String studentLevel = studentLevelDeterminer  == 1 ? "junior" : "senior";

        String studentName = generateRandomName();
        return new Student(studentId, studentName, studentLevel);
    }

    private static String generateRandomName() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return firstName + " " + lastName;
    }

    public static List<Student> generateRandomStudents(int count) {
        List<Student> students = new ArrayList<>();

        for (int index = 0; index < count; index++) {
            students.add(generateRandomStudent());
        }

        return students;
    }

    public static List<Teacher> generateRandomTeachers(int count) {
        List<Teacher> teachers = new ArrayList<>();
        Set<String> usedDepartments = new HashSet<>();

        for (int index = 0; index < count; index++) {
            Teacher teacher = generateRandomTeacher();

            // Try to avoid duplicate departments if possible
            while (usedDepartments.contains(teacher.getDepartment()) &&
                    usedDepartments.size() < TEACHER_DEPARTMENTS.length) {
                teacher = generateRandomTeacher();
                teacherIdCounter--; // Adjust counter since we're regenerating
            }

            usedDepartments.add(teacher.getDepartment());
            teachers.add(teacher);
        }

        return teachers;
    }
}
