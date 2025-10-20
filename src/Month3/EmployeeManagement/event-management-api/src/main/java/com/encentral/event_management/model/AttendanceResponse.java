package com.encentral.event_management.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private boolean success;
    private String message;
    private Attendance attendance;
}
