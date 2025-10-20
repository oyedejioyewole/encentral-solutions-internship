package com.encentral.event_management.model;

import com.encentral.entities.AttendanceStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    private String attendanceId;
    private String employeeId;
    private LocalDate date;
    private AttendanceStatus status;
    private LocalDateTime checkInTime;
}