package com.api.eventify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String userId;
    private String name;
    private String email;

    public AuthResponse(
        String token,
        String userId,
        String name,
        String email
    ) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}
