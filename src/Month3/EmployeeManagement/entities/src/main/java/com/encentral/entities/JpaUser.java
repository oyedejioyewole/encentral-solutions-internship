package com.encentral.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class JpaUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Should be hashed

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String department;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(
        mappedBy = "employee",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private List<JpaAttendanceRecord> attendanceRecords;

    public JpaUser(
        String email,
        String password,
        String firstName,
        String lastName,
        UserRole role,
        LocalDate joinDate
    ) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

enum UserRole {
    ADMIN,
    EMPLOYEE,
}
