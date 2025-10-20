package com.encentral.event_management.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private String message;
    private String pin;
    private User employee;
}