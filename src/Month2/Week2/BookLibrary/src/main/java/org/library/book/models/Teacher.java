package org.library.book.models;

import java.util.Locale;

public class Teacher extends User {
    private final String department;

    public Teacher(String teacherId, String teacherName, String department) {
        super(teacherId, teacherName, UserType.TEACHER);
        this.department = department;
    }

    public String getDepartment() { return department; }

    @Override
    public int getPriorityLevel() {
        return 1;
    }

    @Override
    public String toString() {
        return String.format("%s (is a %s in %s department) with this ID: %s", super.getName(), super.getUserType().name().toLowerCase(), this.department, super.getId());
    }
}
