package com.smartcare.repository;

import com.smartcare.model.Appointment;
import com.smartcare.model.AppointmentStatus;
import com.smartcare.model.Doctor;
import com.smartcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByPatientOrderByAppointmentDateTimeDesc(User patient);
    
    List<Appointment> findByDoctorOrderByAppointmentDateTimeDesc(Doctor doctor);
    
    List<Appointment> findByPatientAndStatus(User patient, AppointmentStatus status);
    
    List<Appointment> findByDoctorAndStatus(Doctor doctor, AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND " +
           "a.appointmentDateTime BETWEEN :startDate AND :endDate AND " +
           "a.status IN ('SCHEDULED', 'CONFIRMED')")
    List<Appointment> findDoctorAppointmentsInRange(
        @Param("doctor") Doctor doctor,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND " +
           "a.appointmentDateTime >= :now ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingAppointmentsByPatient(
        @Param("patient") User patient,
        @Param("now") LocalDateTime now
    );
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND " +
           "a.appointmentDateTime >= :now ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingAppointmentsByDoctor(
        @Param("doctor") Doctor doctor,
        @Param("now") LocalDateTime now
    );
}
