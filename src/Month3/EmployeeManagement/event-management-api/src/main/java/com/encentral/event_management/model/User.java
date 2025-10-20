package com.encentral.event_management.model;

import com.encentral.entities.UserRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String department;
    private String token;
}