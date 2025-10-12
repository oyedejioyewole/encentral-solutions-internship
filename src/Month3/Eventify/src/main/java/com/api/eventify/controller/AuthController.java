package com.api.eventify.controller;

import com.api.eventify.dto.AuthResponse;
import com.api.eventify.dto.LoginRequest;
import com.api.eventify.dto.SignupRequest;
import com.api.eventify.model.User;
import com.api.eventify.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided credentials"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "User registered successfully"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input or email already exists"
            ),
        }
    )
    public ResponseEntity<AuthResponse> signup(
        @Valid @RequestBody SignupRequest request
    ) {
        User user = authService.signup(
            request.getName(),
            request.getEmail(),
            request.getPassword()
        );
        String token = authService.login(
            request.getEmail(),
            request.getPassword()
        );

        AuthResponse response = new AuthResponse(
            token,
            user.getId(),
            user.getName(),
            user.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login user",
        description = "Authenticates user and returns JWT token"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Login successful"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Invalid credentials"
            ),
        }
    )
    public ResponseEntity<AuthResponse> login(
        @Valid @RequestBody LoginRequest request
    ) {
        String token = authService.login(
            request.getEmail(),
            request.getPassword()
        );
        User user = authService.getUserByEmail(request.getEmail());

        AuthResponse response = new AuthResponse(
            token,
            user.getId(),
            user.getName(),
            user.getEmail()
        );

        return ResponseEntity.ok(response);
    }
}
