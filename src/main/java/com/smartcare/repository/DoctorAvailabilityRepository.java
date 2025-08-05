package com.smartcare.repository;

import com.smartcare.model.DayOfWeek;
import com.smartcare.model.Doctor;
import com.smartcare.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctorAndIsActiveTrue(Doctor doctor);
    List<DoctorAvailability> findByDoctorAndDayOfWeekAndIsActiveTrue(Doctor doctor, DayOfWeek dayOfWeek);
}
