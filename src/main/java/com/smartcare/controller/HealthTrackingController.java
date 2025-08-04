package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.dto.healthtracking.HealthTrackingOverviewDto;
import com.smartcare.dto.healthtracking.HealthGoalDto;
import com.smartcare.service.HealthTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(HealthTrackingController.class);
    
    @Autowired
    private HealthTrackingService healthTrackingService;

    @Operation(summary = "Get health tracking overview")
    @GetMapping("/overview")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HealthTrackingOverviewDto> getOverview(Principal principal) {
        long startTime = System.currentTimeMillis();
        logger.info("HealthTrackingController | getOverview | method entry");
        
        Long userId = getUserIdFromPrincipal(principal);
        HealthTrackingOverviewDto result = healthTrackingService.getOverview(userId);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("HealthTrackingController | getOverview | method exit with {}ms", executionTime);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get all health goals")
    @GetMapping("/goals")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<HealthGoalDto>> getGoals(Principal principal) {
        long startTime = System.currentTimeMillis();
        logger.info("HealthTrackingController | getGoals | method entry");
        
        Long userId = getUserIdFromPrincipal(principal);
        List<HealthGoalDto> result = healthTrackingService.getGoals(userId);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("HealthTrackingController | getGoals | method exit with {}ms", executionTime);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Add a new health goal")
    @PostMapping("/goals")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HealthGoalDto> addGoal(Principal principal, @Valid @RequestBody HealthGoalDto goalDto) {
        long startTime = System.currentTimeMillis();
        logger.info("HealthTrackingController | addGoal | method entry");
        
        Long userId = getUserIdFromPrincipal(principal);
        HealthGoalDto result = healthTrackingService.addGoal(userId, goalDto);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("HealthTrackingController | addGoal | method exit with {}ms", executionTime);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update a health goal")
    @PutMapping("/goals/{goalId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HealthGoalDto> updateGoal(Principal principal, @PathVariable Long goalId, @Valid @RequestBody HealthGoalDto goalDto) {
        long startTime = System.currentTimeMillis();
        logger.info("HealthTrackingController | updateGoal | method entry");
        
        Long userId = getUserIdFromPrincipal(principal);
        HealthGoalDto result = healthTrackingService.updateGoal(userId, goalId, goalDto);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("HealthTrackingController | updateGoal | method exit with {}ms", executionTime);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete a health goal")
    @DeleteMapping("/goals/{goalId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> deleteGoal(Principal principal, @PathVariable Long goalId) {
        long startTime = System.currentTimeMillis();
        logger.info("HealthTrackingController | deleteGoal | method entry");
        
        Long userId = getUserIdFromPrincipal(principal);
        healthTrackingService.deleteGoal(userId, goalId);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("HealthTrackingController | deleteGoal | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Goal deleted successfully"));
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        // Implement logic to extract userId from Principal or JWT claims
        // For now, assume username is userId (convert as needed)
        return Long.valueOf(principal.getName());
    }
}
