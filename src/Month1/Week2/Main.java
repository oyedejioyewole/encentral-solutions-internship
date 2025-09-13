package Month1.Week2;

import java.util.*;
import java.util.stream.Collectors;

// TODO: Refactor this code. It currently dumps records into an ArrayList for processing which isn't optimal.

// Quote:
// "Everything in life has a 0.1% chance at breaking (including this code)
// please don't poke around too much :)"
public class Main {

    // Student class to represent each student record
    static class Student {
        private final String className;
        private final String studentId;
        private final String studentName;
        private final String subject;
        private final double totalScore;

        public Student(String className, String studentId, String studentName,
                       String subject, double ca1Score, double ca2Score, double examScore) {
            this.className = className;
            this.studentId = studentId;
            this.studentName = studentName;
            this.subject = subject;
            this.totalScore = ca1Score + ca2Score + examScore;
        }

        // Getters
        public String getClassName() { return className; }
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getSubject() { return subject; }
        public double getTotalScore() { return totalScore; }
    }

    // Class to represent aggregated student results
    static class StudentResult {
        private final String studentId;
        private final String studentName;
        private final String className;
        private double totalScore;
        private final List<Student> subjects;
        private int position;

        public StudentResult(String studentId, String studentName, String className) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.className = className;
            this.subjects = new ArrayList<>();
            this.totalScore = 0.0;
        }

        public void addSubject(Student student) {
            this.subjects.add(student);
            this.totalScore += student.getTotalScore();
        }

        public double getAverageScore() {
            return subjects.isEmpty() ? 0.0 : totalScore / subjects.size();
        }

        // Getters
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getClassName() { return className; }
        public List<Student> getSubjects() { return subjects; }
        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
    }

    private static List<Student> parseCSVData(String csvData) {
        List<Student> students = new ArrayList<>();
        String[] lines = csvData.trim().split("\\n");

        // Skip header row
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            if (fields.length >= 7) {
                try {
                    String className = fields[0].trim();
                    String studentId = fields[1].trim();
                    String studentName = fields[2].trim();
                    String subject = fields[3].trim();
                    double ca1Score = Double.parseDouble(fields[4].trim());
                    double ca2Score = Double.parseDouble(fields[5].trim());
                    double examScore = Double.parseDouble(fields[6].trim());

                    students.add(new Student(className, studentId, studentName,
                            subject, ca1Score, ca2Score, examScore));
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing line " + (i + 1) + ": " + lines[i]);
                }
            }
        }
        return students;
    }

    private static void showClassResult(List<Student> allStudents, String targetClass) {
        // Filter students by class
        List<Student> classStudents = allStudents.stream()
                .filter(s -> s.getClassName().equalsIgnoreCase(targetClass))
                .toList();

        if (classStudents.isEmpty()) {
            System.out.println("No students found in class: " + targetClass);
            return;
        }

        // Group by student and calculate totals
        Map<String, StudentResult> studentResults = new HashMap<>();

        for (Student student : classStudents) {
            String key = student.getStudentId();
            studentResults.putIfAbsent(key, new StudentResult(
                    student.getStudentId(),
                    student.getStudentName(),
                    student.getClassName()
            ));
            studentResults.get(key).addSubject(student);
        }

        // Sort by average score descending and assign positions
        List<StudentResult> sortedResults = studentResults.values().stream()
                .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
                .toList();

        // Assign positions
        for (int i = 0; i < sortedResults.size(); i++) {
            sortedResults.get(i).setPosition(i + 1);
        }

        // Get all unique subjects in the class to create header
        Set<String> allSubjects = classStudents.stream()
                .map(Student::getSubject)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Print header
        System.out.print("SN,Class,Student ID Student_Name");
        for (String subject : allSubjects) {
            System.out.print("," + subject);
        }
        System.out.println(",Position");

        // Print results for each student in position order
        for (StudentResult result : sortedResults) {
            // Create a map of subject -> total score for this student
            Map<String, Double> subjectScores = result.getSubjects().stream()
                    .collect(Collectors.toMap(
                            Student::getSubject,
                            Student::getTotalScore
                    ));

            // Print student info
            System.out.printf("%d,%s,%s,%s",
                    result.getPosition(),
                    result.getClassName(),
                    result.getStudentId(),
                    result.getStudentName()
            );

            // Print scores for each subject (in order of header)
            for (String subject : allSubjects) {
                Double score = subjectScores.get(subject);
                if (score != null) {
                    System.out.printf(",%.0f", score);
                } else {
                    System.out.print(",0"); // Default for missing subjects
                }
            }

            // Print position
            System.out.printf(",%d%n", result.getPosition());
        }
    }

    private static void showStudentResult(List<Student> allStudents, String targetStudentId) {
        // Filter by student ID
        List<Student> studentSubjects = allStudents.stream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(targetStudentId))
                .toList();

        if (studentSubjects.isEmpty()) {
            System.out.println("No student found with ID: " + targetStudentId);
            return;
        }

        // Get student info
        Student firstRecord = studentSubjects.getFirst();
        String studentName = firstRecord.getStudentName();
        String className = firstRecord.getClassName();

        // Get all students in the same class to calculate position
        List<Student> classStudents = allStudents.stream()
                .filter(s -> s.getClassName().equals(className))
                .toList();

        // Group by student and calculate averages
        Map<String, StudentResult> studentResults = new HashMap<>();

        for (Student student : classStudents) {
            String key = student.getStudentId();
            studentResults.putIfAbsent(key, new StudentResult(
                    student.getStudentId(),
                    student.getStudentName(),
                    student.getClassName()
            ));
            studentResults.get(key).addSubject(student);
        }

        // Sort by average score descending and assign positions
        List<StudentResult> sortedResults = studentResults.values().stream()
                .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
                .toList();

        // Find the position of the target student
        int position = 1;
        for (StudentResult result : sortedResults) {
            if (result.getStudentId().equals(targetStudentId)) {
                break;
            }
            position++;
        }

        // Get all unique subjects in the class to create header
        Set<String> allSubjects = classStudents.stream()
                .map(Student::getSubject)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Print header
        System.out.print("SN,Class,Student ID,Student Name");
        for (String subject : allSubjects) {
            System.out.print("," + subject);
        }
        System.out.println(",Position");

        // Create a map of subject -> total score for this student
        Map<String, Double> subjectScores = studentSubjects.stream()
                .collect(Collectors.toMap(
                        Student::getSubject,
                        Student::getTotalScore
                ));

        // Print student info
        System.out.printf("%d,%s,%s,%s",
                position,
                className,
                targetStudentId,
                studentName
        );

        // Print scores for each subject (in order of header)
        for (String subject : allSubjects) {
            Double score = subjectScores.get(subject);
            if (score != null) {
                System.out.printf(",%.0f", score);
            } else {
                System.out.print(",0"); // Default for missing subjects
            }
        }

        // Print position
        System.out.printf(",%d%n", position);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Sample CSV data (you can replace this with actual CSV input)
        String csvData = """
Class,Student ID,Student Name,Subject,CA1 Score,CA2 Score,Exam Score
SS1,SN001,Adekune Gold,Math,10,12,60
SS1,SN001,Adekune Gold,English,13,5,56
SS1,SN001,Adekune Gold,Biology,9,6,46
SS1,SN001,Adekune Gold,Economics,5,14,48
SS1,SN001,Adekune Gold,Geography,8,15,45
SS1,SN002,Simi Gold,Math,15,13,49
SS1,SN002,Simi Gold,English,12,6,56
SS1,SN002,Simi Gold,Biology,9,8,35
SS1,SN002,Simi Gold,Economics,7,4,63
SS1,SN002,Simi Gold,Geography,10,11,56
SS1,SN003,Adamu Abdul,Math,11,10,37
SS1,SN003,Adamu Abdul,English,14,4,58
SS1,SN003,Adamu Abdul,Biology,7,9,65
SS1,SN003,Adamu Abdul,Economics,9,7,66
SS1,SN003,Adamu Abdul,Geography,4,8,52
SS1,SN004,Ada Amaka,Math,11,4,49
SS1,SN004,Ada Amaka,English,13,15,43
SS1,SN004,Ada Amaka,Biology,6,13,34
SS1,SN004,Ada Amaka,Economics,8,8,27
SS1,SN004,Ada Amaka,Geography,3,9,38
SS1,SN005,Kelly Handsome,Math,12,5,56
SS1,SN005,Kelly Handsome,English,14,10,70
SS1,SN005,Kelly Handsome,Biology,13,10,34
SS1,SN005,Kelly Handsome,Economics,9,11,62
SS1,SN005,Kelly Handsome,Geography,10,8,38
SS1,SN006,Billy King,Math,6,9,48
SS1,SN006,Billy King,English,7,8,56
SS1,SN006,Billy King,Biology,14,4,63
SS1,SN006,Billy King,Economics,12,11,59
SS1,SN006,Billy King,Geography,6,1,34
SS2,SN007,Onyekachukwu Chiamaka,Math,7,12,65
SS2,SN007,Onyekachukwu Chiamaka,English,14,6,63
SS2,SN007,Onyekachukwu Chiamaka,Biology,12,3,45
SS2,SN007,Onyekachukwu Chiamaka,Economics,9,4,61
SS2,SN007,Onyekachukwu Chiamaka,Geography,5,9,34
SS2,SN008,Chinyelu Onyekachi,Math,11,7,54
SS2,SN008,Chinyelu Onyekachi,English,12,12,12
SS2,SN008,Chinyelu Onyekachi,Biology,9,11,39
SS2,SN008,Chinyelu Onyekachi,Economics,9,4,57
SS2,SN008,Chinyelu Onyekachi,Geography,7,8,61
SS2,SN009,Akuchi Ekwueme,Math,10,5,29
SS2,SN009,Akuchi Ekwueme,English,5,7,31
SS2,SN009,Akuchi Ekwueme,Biology,6,9,78
SS2,SN009,Akuchi Ekwueme,Economics,9,3,34
SS2,SN009,Akuchi Ekwueme,Geography,6,7,54
SS2,SN010,Ekwueme Chibuike,Math,2,4,34
SS2,SN010,Ekwueme Chibuike,English,14,8,65
SS2,SN010,Ekwueme Chibuike,Biology,12,5,34
SS2,SN010,Ekwueme Chibuike,Economics,8,8,27
SS2,SN010,Ekwueme Chibuike,Geography,12,8,34
SS2,SN011,Chidimma Nwanneka,Math,8,4,61
SS2,SN011,Chidimma Nwanneka,English,14,6,34
SS2,SN011,Chidimma Nwanneka,Biology,13,9,68
SS2,SN011,Chidimma Nwanneka,Economics,7,11,46
SS2,SN011,Chidimma Nwanneka,Geography,10,12,48
SS2,SN012,Ifeoma Okeke,Math,9,3,56
SS2,SN012,Ifeoma Okeke,English,12,5,51
SS2,SN012,Ifeoma Okeke,Biology,4,7,45
SS2,SN012,Ifeoma Okeke,Economics,8,11,64
SS2,SN012,Ifeoma Okeke,Geography,6,10,34
SS3,SN013,Bosede Ade,Math,8,11,34
SS3,SN013,Bosede Ade,English,5,12,27
SS3,SN013,Bosede Ade,Biology,8,9,34
SS3,SN013,Bosede Ade,Economics,3,9,61
SS3,SN013,Bosede Ade,Geography,10,7,54
SS3,SN014,Bose Olufunmilayo,Math,9,10,12
SS3,SN014,Bose Olufunmilayo,English,7,5,39
SS3,SN014,Bose Olufunmilayo,Biology,8,6,57
SS3,SN014,Bose Olufunmilayo,Economics,6,9,61
SS3,SN014,Bose Olufunmilayo,Geography,11,6,52
SS3,SN015,Funmilayo Bolanle,Math,14,2,49
SS3,SN015,Funmilayo Bolanle,English,7,14,43
SS3,SN015,Funmilayo Bolanle,Biology,13,12,34
SS3,SN015,Funmilayo Bolanle,Economics,14,8,49
SS3,SN015,Funmilayo Bolanle,Geography,7,12,56
SS3,SN016,Yewande Olufunmilayo,Math,8,8,35
SS3,SN016,Yewande Olufunmilayo,English,4,14,63
SS3,SN016,Yewande Olufunmilayo,Biology,15,13,56
SS3,SN016,Yewande Olufunmilayo,Economics,7,7,34
SS3,SN016,Yewande Olufunmilayo,Geography,8,10,49
SS3,SN017,Taiwo Bosede,Math,4,9,56
SS3,SN017,Taiwo Bosede,English,10,12,57
SS3,SN017,Taiwo Bosede,Biology,13,4,61
SS3,SN017,Taiwo Bosede,Economics,4,8,29
SS3,SN017,Taiwo Bosede,Geography,8,6,45
SS3,SN018,Dada Oluwasegun,Math,9,4,34
SS3,SN018,Dada Oluwasegun,English,4,6,48
SS3,SN018,Dada Oluwasegun,Biology,11,11,34
SS3,SN018,Dada Oluwasegun,Economics,10,13,34
SS3,SN018,Dada Oluwasegun,Geography,3,15,23
""";

        System.out.println("=== Computation System for school results ===");
        System.out.println("Data source (CSV) loaded successfully!");
        System.out.println();

        // Parse CSV data
        List<Student> allStudents = parseCSVData(csvData);

        if (allStudents.isEmpty()) {
            System.out.println("No valid student data found!");
            return;
        }

        while (true) {
            System.out.println("What operation do you want to perform?");
            System.out.println("Enter 1 to show result for a class");
            System.out.println("Enter 2 to show result for a student");
            System.out.println("Enter 0 to exit");
            System.out.print("Your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter class name: ");
                        String className = scanner.nextLine().trim();
                        System.out.println();
                        showClassResult(allStudents, className);
                        break;

                    case 2:
                        System.out.print("Enter Student ID: ");
                        String studentId = scanner.nextLine().trim();
                        System.out.println();
                        showStudentResult(allStudents, studentId);
                        break;

                    case 0:
                        System.out.println("Thank you for using the School Result Computation System!");
                        return;

                    default:
                        System.out.println("Invalid choice! Please enter 1, 2, or 0.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }

            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println();
        }
    }
}