package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.model.Medication;
import com.smartcare.model.MedicationStatus;
import com.smartcare.security.UserPrincipal;
import com.smartcare.service.MedicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medications")
@Tag(name = "Medications", description = "Medication management and reminder endpoints")
public class MedicationController {

    private static final Logger logger = LoggerFactory.getLogger(MedicationController.class);

    @Autowired
    private MedicationService medicationService;

    @PostMapping
    @Operation(summary = "Add medication", description = "Add a new medication to user's profile")
    public ResponseEntity<?> addMedication(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody Medication medication) {
        long startTime = System.currentTimeMillis();
        logger.info("MedicationController | addMedication | method entry");
        
        try {
            Medication savedMedication = medicationService.addMedication(currentUser.getId(), medication);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("MedicationController | addMedication | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Medication added successfully", savedMedication));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("MedicationController | addMedication | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get user medications", description = "Retrieve all medications for current user")
    public ResponseEntity<?> getUserMedications(@AuthenticationPrincipal UserPrincipal currentUser) {
        long startTime = System.currentTimeMillis();
        logger.info("MedicationController | getUserMedications | method entry");
        
        List<Medication> medications = medicationService.getUserMedications(currentUser.getId());
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("MedicationController | getUserMedications | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Medications retrieved successfully", medications));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active medications", description = "Retrieve active medications for current user")
    public ResponseEntity<?> getActiveMedications(@AuthenticationPrincipal UserPrincipal currentUser) {
        long startTime = System.currentTimeMillis();
        logger.info("MedicationController | getActiveMedications | method entry");
        
        List<Medication> medications = medicationService.getActiveMedications(currentUser.getId());
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("MedicationController | getActiveMedications | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Active medications retrieved successfully", medications));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medication by ID", description = "Retrieve medication details by ID")
    public ResponseEntity<?> getMedicationById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        logger.info("MedicationController | getMedicationById | method entry");
        
        try {
            ResponseEntity<?> response = medicationService.getMedicationById(id)
                    .map(medication -> ResponseEntity.ok(new ApiResponse(true, "Medication found", medication)))
                    .orElse(ResponseEntity.notFound().build());
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("MedicationController | getMedicationById | method exit with {}ms", executionTime);
            return response;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("MedicationController | getMedicationById | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update medication", description = "Update medication information")
    public ResponseEntity<?> updateMedication(
            @PathVariable Long id,
            @Valid @RequestBody Medication medicationDetails) {
        long startTime = System.currentTimeMillis();
        logger.info("MedicationController | updateMedication | method entry");
        
        try {
            Medication updatedMedication = medicationService.updateMedication(id, medicationDetails);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("MedicationController | updateMedication | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Medication updated successfully", updatedMedication));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("MedicationController | updateMedication | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update medication status", description = "Update the status of a medication")
    public ResponseEntity<?> updateMedicationStatus(
            @PathVariable Long id,
            @RequestParam MedicationStatus status) {
        long startTime = System.currentTimeMillis();
        logger.info("MedicationController | updateMedicationStatus | method entry");
        
        try {
            Medication medication = medicationService.updateMedicationStatus(id, status);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("MedicationController | updateMedicationStatus | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Medication status updated successfully", medication));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("MedicationController | updateMedicationStatus | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}/quantity")
    @Operation(summary = "Update remaining quantity", description = "Update remaining quantity of medication")
    public ResponseEntity<?> updateRemainingQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        long startTime = System.currentTimeMillis();
        logger.info("MedicationController | updateRemainingQuantity | method entry");
        
        try {
            Medication medication = medicationService.updateRemainingQuantity(id, quantity);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("MedicationController | updateRemainingQuantity | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Quantity updated successfully", medication));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("MedicationController | updateRemainingQuantity | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medication", description = "Remove medication from user's profile")
    public ResponseEntity<?> deleteMedication(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        logger.info("MedicationController | deleteMedication | method entry");
        
        try {
            medicationService.deleteMedication(id);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("MedicationController | deleteMedication | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Medication deleted successfully"));
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("MedicationController | deleteMedication | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to delete medication"));
        }
    }

    @GetMapping("/refill-needed")
    @Operation(summary = "Get medications needing refill", description = "Get medications that need refilling")
    public ResponseEntity<?> getMedicationsNeedingRefill() {
        long startTime = System.currentTimeMillis();
        logger.info("MedicationController | getMedicationsNeedingRefill | method entry");
        
        List<Medication> medications = medicationService.getMedicationsNeedingRefill();
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("MedicationController | getMedicationsNeedingRefill | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Medications needing refill retrieved successfully", medications));
    }

    @GetMapping("/by-status")
    @Operation(summary = "Get medications by status", description = "Get user medications filtered by status")
    public ResponseEntity<?> getMedicationsByStatus(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam MedicationStatus status) {
        long startTime = System.currentTimeMillis();
        logger.info("MedicationController | getMedicationsByStatus | method entry");
        
        List<Medication> medications = medicationService.getMedicationsByStatus(currentUser.getId(), status);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("MedicationController | getMedicationsByStatus | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Medications retrieved successfully", medications));
    }
}
