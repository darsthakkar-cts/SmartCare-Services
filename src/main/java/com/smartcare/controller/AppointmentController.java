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

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/book")
    @Operation(summary = "Book appointment", description = "Book a new appointment with a doctor")
    public ResponseEntity<?> bookAppointment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody AppointmentBookingRequest request) {
        try {
            Appointment appointment = appointmentService.bookAppointment(
                    currentUser.getId(),
                    request.getDoctorId(),
                    request.getAppointmentDateTime(),
                    request.getReason(),
                    request.getType()
            );
            return ResponseEntity.ok(new ApiResponse(true, "Appointment booked successfully", appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/my-appointments")
    @Operation(summary = "Get patient appointments", description = "Retrieve all appointments for current patient")
    public ResponseEntity<?> getMyAppointments(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<Appointment> appointments = appointmentService.getPatientAppointments(currentUser.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Appointments retrieved successfully", appointments));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get doctor appointments", description = "Retrieve all appointments for a specific doctor")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long doctorId) {
        List<Appointment> appointments = appointmentService.getDoctorAppointments(doctorId);
        return ResponseEntity.ok(new ApiResponse(true, "Doctor appointments retrieved successfully", appointments));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming appointments", description = "Get upcoming appointments for current user")
    public ResponseEntity<?> getUpcomingAppointments(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "patient") String userType) {
        List<Appointment> appointments = appointmentService.getUpcomingAppointments(currentUser.getId(), userType);
        return ResponseEntity.ok(new ApiResponse(true, "Upcoming appointments retrieved successfully", appointments));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Retrieve appointment details by ID")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(appointment -> ResponseEntity.ok(new ApiResponse(true, "Appointment found", appointment)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update appointment status", description = "Update the status of an appointment")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        try {
            Appointment appointment = appointmentService.updateAppointmentStatus(id, status);
            return ResponseEntity.ok(new ApiResponse(true, "Appointment status updated successfully", appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}/notes")
    @Operation(summary = "Add appointment notes", description = "Add clinical notes to an appointment")
    public ResponseEntity<?> addAppointmentNotes(
            @PathVariable Long id,
            @RequestParam String notes) {
        try {
            Appointment appointment = appointmentService.addAppointmentNotes(id, notes);
            return ResponseEntity.ok(new ApiResponse(true, "Notes added successfully", appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel appointment", description = "Cancel an existing appointment")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        try {
            appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(new ApiResponse(true, "Appointment cancelled successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/available-slots")
    @Operation(summary = "Get available time slots", description = "Get available appointment slots for a doctor on a specific date")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam String date) {
        try {
            LocalDateTime appointmentDate = LocalDateTime.parse(date);
            List<LocalDateTime> slots = appointmentService.getAvailableSlots(doctorId, appointmentDate);
            return ResponseEntity.ok(new ApiResponse(true, "Available slots retrieved successfully", slots));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid date format"));
        }
    }
}
