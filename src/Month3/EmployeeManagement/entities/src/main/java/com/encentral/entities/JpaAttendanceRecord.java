package com.encentral.entities;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(
    name = "attendance_records",
    uniqueConstraints = @UniqueConstraint(
        columnNames = { "employee_id", "date" }
    )
)
@Data
@NoArgsConstructor
public class JpaAttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private JpaUser employee;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public JpaAttendanceRecord(
        JpaUser employee,
        LocalDate date,
        AttendanceStatus status
    ) {
        this.employee = employee;
        this.date = date;
        this.status = status;
        this.status = AttendanceStatus.ABSENT;
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

    // Helper method to calculate work hours
    public Double calculateWorkHours() {
        if (checkInTime != null && checkOutTime != null) {
            Duration duration = Duration.between(checkInTime, checkOutTime);
            return duration.toMinutes() / 60.0;
        }
        return null;
    }
}

enum AttendanceStatus {
    PRESENT,
    ABSENT,
}
