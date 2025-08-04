package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.dto.healthtracking.HealthTrackingOverviewDto;
import com.smartcare.dto.healthtracking.HealthGoalDto;
import com.smartcare.service.HealthTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/healthtracking")
@Tag(name = "Health Tracking", description = "APIs for health tracking and goals management")
public class HealthTrackingController {
    @Autowired
    private HealthTrackingService healthTrackingService;

    @Operation(summary = "Get health tracking overview")
    @GetMapping("/overview")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HealthTrackingOverviewDto> getOverview(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(healthTrackingService.getOverview(userId));
    }

    @Operation(summary = "Get all health goals")
    @GetMapping("/goals")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<HealthGoalDto>> getGoals(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(healthTrackingService.getGoals(userId));
    }

    @Operation(summary = "Add a new health goal")
    @PostMapping("/goals")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HealthGoalDto> addGoal(Principal principal, @Valid @RequestBody HealthGoalDto goalDto) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(healthTrackingService.addGoal(userId, goalDto));
    }

    @Operation(summary = "Update a health goal")
    @PutMapping("/goals/{goalId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HealthGoalDto> updateGoal(Principal principal, @PathVariable Long goalId, @Valid @RequestBody HealthGoalDto goalDto) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(healthTrackingService.updateGoal(userId, goalId, goalDto));
    }

    @Operation(summary = "Delete a health goal")
    @DeleteMapping("/goals/{goalId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> deleteGoal(Principal principal, @PathVariable Long goalId) {
        Long userId = getUserIdFromPrincipal(principal);
        healthTrackingService.deleteGoal(userId, goalId);
        return ResponseEntity.ok(new ApiResponse(true, "Goal deleted successfully"));
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        // Implement logic to extract userId from Principal or JWT claims
        // For now, assume username is userId (convert as needed)
        return Long.valueOf(principal.getName());
    }
}
