package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.model.Doctor;
import com.smartcare.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@Tag(name = "Doctors", description = "Doctor management and search endpoints")
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/search")
    @Operation(summary = "Search doctors", description = "Search doctors by various criteria")
    public ResponseEntity<?> searchDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        long startTime = System.currentTimeMillis();
        logger.info("DoctorController | searchDoctors | method entry");
        
        Page<Doctor> doctors = doctorService.searchDoctors(specialization, city, state, 
                                                          language, minRating, page, size);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("DoctorController | searchDoctors | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Doctors retrieved successfully", doctors));
    }

    @GetMapping
    @Operation(summary = "Get all active doctors", description = "Retrieve all active doctors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllDoctors() {
        long startTime = System.currentTimeMillis();
        logger.info("DoctorController | getAllDoctors | method entry");
        
        List<Doctor> doctors = doctorService.getAllActiveDoctors();
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("DoctorController | getAllDoctors | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Doctors retrieved successfully", doctors));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID", description = "Retrieve doctor details by ID")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        logger.info("DoctorController | getDoctorById | method entry");
        
        try {
            ResponseEntity<?> response = doctorService.getDoctorById(id)
                    .map(doctor -> ResponseEntity.ok(new ApiResponse(true, "Doctor found", doctor)))
                    .orElse(ResponseEntity.notFound().build());
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("DoctorController | getDoctorById | method exit with {}ms", executionTime);
            return response;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("DoctorController | getDoctorById | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "Create new doctor", description = "Add a new doctor to the system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDoctor(@RequestBody Doctor doctor) {
        long startTime = System.currentTimeMillis();
        logger.info("DoctorController | createDoctor | method entry");
        
        Doctor savedDoctor = doctorService.saveDoctor(doctor);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("DoctorController | createDoctor | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Doctor created successfully", savedDoctor));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update doctor", description = "Update doctor information")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        long startTime = System.currentTimeMillis();
        logger.info("DoctorController | updateDoctor | method entry");
        
        try {
            Doctor updatedDoctor = doctorService.updateDoctor(id, doctorDetails);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("DoctorController | updateDoctor | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Doctor updated successfully", updatedDoctor));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("DoctorController | updateDoctor | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete doctor", description = "Remove doctor from the system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        logger.info("DoctorController | deleteDoctor | method entry");
        
        doctorService.deleteDoctor(id);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("DoctorController | deleteDoctor | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Doctor deleted successfully"));
    }

    @GetMapping("/by-specialization")
    @Operation(summary = "Get doctors by specialization", description = "Find doctors by specialization")
    public ResponseEntity<?> getDoctorsBySpecialization(@RequestParam String specialization) {
        long startTime = System.currentTimeMillis();
        logger.info("DoctorController | getDoctorsBySpecialization | method entry");
        
        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("DoctorController | getDoctorsBySpecialization | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Doctors retrieved successfully", doctors));
    }
}
