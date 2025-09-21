package org.library.book.model;

import jakarta.persistence.*;

@Entity
public class Student {
    @Id
    @Column (name = "student_id")
    private String studentId;

    @Column (name = "student_name")
    private String studentName;

    public Student(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
    }

    Student() {}

    public String getStudentId() { return this.studentId; }
    public String getStudentName() { return this.studentName; }

    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s'}", this.studentId, this.studentName);
    }
}
