package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.dto.AppointmentBookingRequest;
import com.smartcare.model.Appointment;
import com.smartcare.model.AppointmentStatus;
import com.smartcare.security.UserPrincipal;
import com.smartcare.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Appointments", description = "Appointment booking and management endpoints")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/book")
    @Operation(summary = "Book appointment", description = "Book a new appointment with a doctor")
    public ResponseEntity<?> bookAppointment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody AppointmentBookingRequest request) {
        long startTime = System.currentTimeMillis();
        logger.info("AppointmentController | bookAppointment | method entry");
        
        try {
            Appointment appointment = appointmentService.bookAppointment(
                    currentUser.getId(),
                    request.getDoctorId(),
                    request.getAppointmentDateTime(),
                    request.getReason(),
                    request.getType()
            );
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("AppointmentController | bookAppointment | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Appointment booked successfully", appointment));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("AppointmentController | bookAppointment | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/my-appointments")
    @Operation(summary = "Get patient appointments", description = "Retrieve all appointments for current patient")
    public ResponseEntity<?> getMyAppointments(@AuthenticationPrincipal UserPrincipal currentUser) {
        long startTime = System.currentTimeMillis();
        logger.info("AppointmentController | getMyAppointments | method entry");
        
        List<Appointment> appointments = appointmentService.getPatientAppointments(currentUser.getId());
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("AppointmentController | getMyAppointments | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Appointments retrieved successfully", appointments));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get doctor appointments", description = "Retrieve all appointments for a specific doctor")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long doctorId) {
        long startTime = System.currentTimeMillis();
        logger.info("AppointmentController | getDoctorAppointments | method entry");
        
        List<Appointment> appointments = appointmentService.getDoctorAppointments(doctorId);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("AppointmentController | getDoctorAppointments | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Doctor appointments retrieved successfully", appointments));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming appointments", description = "Get upcoming appointments for current user")
    public ResponseEntity<?> getUpcomingAppointments(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "patient") String userType) {
        long startTime = System.currentTimeMillis();
        logger.info("AppointmentController | getUpcomingAppointments | method entry");
        
        List<Appointment> appointments = appointmentService.getUpcomingAppointments(currentUser.getId(), userType);
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("AppointmentController | getUpcomingAppointments | method exit with {}ms", executionTime);
        return ResponseEntity.ok(new ApiResponse(true, "Upcoming appointments retrieved successfully", appointments));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Retrieve appointment details by ID")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        logger.info("AppointmentController | getAppointmentById | method entry");
        
        ResponseEntity<?> response = appointmentService.getAppointmentById(id)
                .map(appointment -> ResponseEntity.ok(new ApiResponse(true, "Appointment found", appointment)))
                .orElse(ResponseEntity.notFound().build());
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("AppointmentController | getAppointmentById | method exit with {}ms", executionTime);
        return response;
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update appointment status", description = "Update the status of an appointment")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        long startTime = System.currentTimeMillis();
        logger.info("AppointmentController | updateAppointmentStatus | method entry");
        
        try {
            Appointment appointment = appointmentService.updateAppointmentStatus(id, status);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("AppointmentController | updateAppointmentStatus | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Appointment status updated successfully", appointment));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("AppointmentController | updateAppointmentStatus | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}/notes")
    @Operation(summary = "Add appointment notes", description = "Add clinical notes to an appointment")
    public ResponseEntity<?> addAppointmentNotes(
            @PathVariable Long id,
            @RequestParam String notes) {
        long startTime = System.currentTimeMillis();
        logger.info("AppointmentController | addAppointmentNotes | method entry");
        
        try {
            Appointment appointment = appointmentService.addAppointmentNotes(id, notes);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("AppointmentController | addAppointmentNotes | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Notes added successfully", appointment));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("AppointmentController | addAppointmentNotes | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel appointment", description = "Cancel an existing appointment")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        logger.info("AppointmentController | cancelAppointment | method entry");
        
        try {
            appointmentService.cancelAppointment(id);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("AppointmentController | cancelAppointment | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Appointment cancelled successfully"));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("AppointmentController | cancelAppointment | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/available-slots")
    @Operation(summary = "Get available time slots", description = "Get available appointment slots for a doctor on a specific date")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam String date) {
        long startTime = System.currentTimeMillis();
        logger.info("AppointmentController | getAvailableSlots | method entry");
        
        try {
            LocalDateTime appointmentDate = LocalDateTime.parse(date);
            List<LocalDateTime> slots = appointmentService.getAvailableSlots(doctorId, appointmentDate);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("AppointmentController | getAvailableSlots | method exit with {}ms", executionTime);
            return ResponseEntity.ok(new ApiResponse(true, "Available slots retrieved successfully", slots));
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("AppointmentController | getAvailableSlots | method exit with Error : {}. after ms: {}", 
                        executionTime, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid date format"));
        }
    }
}
