package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.model.HealthProfile;
import com.smartcare.model.User;
import com.smartcare.security.UserPrincipal;
import com.smartcare.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@Tag(name = "User Profile", description = "User profile and health profile management endpoints")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping
    @Operation(summary = "Get user profile", description = "Retrieve current user's profile information")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        User user = userProfileService.getUserProfile(currentUser.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Profile retrieved successfully", user));
    }

    @PutMapping
    @Operation(summary = "Update user profile", description = "Update user's basic profile information")
    public ResponseEntity<?> updateUserProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody User userDetails) {
        try {
            User updatedUser = userProfileService.updateUserProfile(currentUser.getId(), userDetails);
            return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Get health profile", description = "Retrieve user's health profile information")
    public ResponseEntity<?> getHealthProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        return userProfileService.getHealthProfile(currentUser.getId())
                .map(healthProfile -> ResponseEntity.ok(new ApiResponse(true, "Health profile found", healthProfile)))
                .orElse(ResponseEntity.ok(new ApiResponse(true, "No health profile found", null)));
    }

    @PostMapping("/health")
    @Operation(summary = "Create/Update health profile", description = "Create or update user's health profile")
    public ResponseEntity<?> createOrUpdateHealthProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody HealthProfile healthProfile) {
        try {
            HealthProfile savedProfile = userProfileService.createOrUpdateHealthProfile(
                    currentUser.getId(), healthProfile);
            return ResponseEntity.ok(new ApiResponse(true, "Health profile saved successfully", savedProfile));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/complete-tour")
    @Operation(summary = "Complete tour", description = "Mark the application tour as completed for the user")
    public ResponseEntity<?> completeTour(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            User updatedUser = userProfileService.completeTour(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Tour completed successfully", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/profile-picture")
    @Operation(summary = "Update profile picture", description = "Update user's profile picture")
    public ResponseEntity<?> updateProfilePicture(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam String profilePictureUrl) {
        try {
            User updatedUser = userProfileService.updateProfilePicture(currentUser.getId(), profilePictureUrl);
            return ResponseEntity.ok(new ApiResponse(true, "Profile picture updated successfully", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
