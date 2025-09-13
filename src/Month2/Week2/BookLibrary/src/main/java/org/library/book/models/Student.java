package org.library.book.models;

public class Student extends User {
    private final String studentLevel;

    public Student(String studentId, String studentName, String seniorityLevel) {
        super(studentId, studentName, UserType.STUDENT);
        this.studentLevel = seniorityLevel;
    }

    public String getStudentLevel() { return studentLevel; }

    public boolean isASenior() { return studentLevel.equals("senior"); }

    @Override
    public int getPriorityLevel() { return studentLevel.equals("senior") ? 2 : 3; }

    @Override
    public String toString() {
        return String.format("%s (is a %s %s) with this ID: %s", super.getName(), this.studentLevel, super.getUserType().name().toLowerCase(), super.getId());
    }
}
