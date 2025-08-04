package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.dto.auth.JwtAuthenticationResponse;
import com.smartcare.dto.auth.LoginRequest;
import com.smartcare.dto.auth.SignUpRequest;
import com.smartcare.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        long startTime = System.currentTimeMillis();
        logger.info("AuthController | authenticateUser | method entry");
        
        try {
            JwtAuthenticationResponse response = authService.authenticateUser(loginRequest);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("AuthController | authenticateUser | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "User signed in successfully", response));
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("AuthController | authenticateUser | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid username/email or password"));
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        long startTime = System.currentTimeMillis();
        logger.info("AuthController | registerUser | method entry");
        
        try {
            authService.registerUser(signUpRequest);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("AuthController | registerUser | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("AuthController | registerUser | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/check-username")
    @Operation(summary = "Check username availability", description = "Check if username is available for registration")
    public ResponseEntity<?> checkUsernameAvailability(@RequestParam String username) {
        long startTime = System.currentTimeMillis();
        logger.info("AuthController | checkUsernameAvailability | method entry");
        
        // Implementation would check username availability
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("AuthController | checkUsernameAvailability | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Username is available"));
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check email availability", description = "Check if email is available for registration")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        long startTime = System.currentTimeMillis();
        logger.info("AuthController | checkEmailAvailability | method entry");
        
        // Implementation would check email availability
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("AuthController | checkEmailAvailability | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Email is available"));
    }
}
