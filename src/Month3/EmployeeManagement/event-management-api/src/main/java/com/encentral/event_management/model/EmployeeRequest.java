package com.encentral.event_management.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String department;
}