package com.smartcare.service;

import com.smartcare.model.*;
import com.smartcare.repository.AppointmentRepository;
import com.smartcare.repository.DoctorRepository;
import com.smartcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public Appointment bookAppointment(Long patientId, Long doctorId, LocalDateTime appointmentDateTime, 
                                     String reason, AppointmentType type) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Check if the slot is available
        List<Appointment> existingAppointments = appointmentRepository.findDoctorAppointmentsInRange(
                doctor, appointmentDateTime, appointmentDateTime.plusMinutes(30));
        
        if (!existingAppointments.isEmpty()) {
            throw new RuntimeException("Time slot is not available");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(appointmentDateTime);
        appointment.setReason(reason);
        appointment.setType(type);
        appointment.setConsultationFee(doctor.getConsultationFee());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return appointmentRepository.findByPatientOrderByAppointmentDateTimeDesc(patient);
    }

    public List<Appointment> getDoctorAppointments(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return appointmentRepository.findByDoctorOrderByAppointmentDateTimeDesc(doctor);
    }

    public List<Appointment> getUpcomingAppointments(Long userId, String userType) {
        LocalDateTime now = LocalDateTime.now();
        
        if ("patient".equals(userType)) {
            User patient = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            return appointmentRepository.findUpcomingAppointmentsByPatient(patient, now);
        } else {
            Doctor doctor = doctorRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            return appointmentRepository.findUpcomingAppointmentsByDoctor(doctor, now);
        }
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public Appointment addAppointmentNotes(Long appointmentId, String notes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setNotes(notes);
        return appointmentRepository.save(appointment);
    }

    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public List<LocalDateTime> getAvailableSlots(Long doctorId, LocalDateTime date) {
        // This is a simplified implementation
        // In a real application, you would check doctor availability and existing appointments
        List<LocalDateTime> slots = List.of(
                date.withHour(9).withMinute(0),
                date.withHour(9).withMinute(30),
                date.withHour(10).withMinute(0),
                date.withHour(10).withMinute(30),
                date.withHour(11).withMinute(0),
                date.withHour(11).withMinute(30),
                date.withHour(14).withMinute(0),
                date.withHour(14).withMinute(30),
                date.withHour(15).withMinute(0),
                date.withHour(15).withMinute(30),
                date.withHour(16).withMinute(0),
                date.withHour(16).withMinute(30)
        );
        
        return slots;
    }
}
