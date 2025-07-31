package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.dto.auth.JwtAuthenticationResponse;
import com.smartcare.dto.auth.LoginRequest;
import com.smartcare.dto.auth.SignUpRequest;
import com.smartcare.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtAuthenticationResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(new ApiResponse(true, "User signed in successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid username/email or password"));
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            authService.registerUser(signUpRequest);
            return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/check-username")
    @Operation(summary = "Check username availability", description = "Check if username is available for registration")
    public ResponseEntity<?> checkUsernameAvailability(@RequestParam String username) {
        // Implementation would check username availability
        return ResponseEntity.ok(new ApiResponse(true, "Username is available"));
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check email availability", description = "Check if email is available for registration")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        // Implementation would check email availability
        return ResponseEntity.ok(new ApiResponse(true, "Email is available"));
    }
}
