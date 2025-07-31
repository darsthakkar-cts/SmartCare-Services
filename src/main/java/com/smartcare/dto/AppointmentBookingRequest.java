package com.smartcare.dto;

import com.smartcare.model.AppointmentType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AppointmentBookingRequest {
    @NotNull
    private Long doctorId;
    
    @NotNull
    private LocalDateTime appointmentDateTime;
    
    private String reason;
    
    private AppointmentType type = AppointmentType.IN_PERSON;

    // Constructors
    public AppointmentBookingRequest() {}

    public AppointmentBookingRequest(Long doctorId, LocalDateTime appointmentDateTime, String reason, AppointmentType type) {
        this.doctorId = doctorId;
        this.appointmentDateTime = appointmentDateTime;
        this.reason = reason;
        this.type = type;
    }

    // Getters and Setters
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public AppointmentType getType() { return type; }
    public void setType(AppointmentType type) { this.type = type; }
}
