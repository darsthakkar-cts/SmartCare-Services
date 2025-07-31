package com.smartcare.repository;

import com.smartcare.model.Medication;
import com.smartcare.model.MedicationStatus;
import com.smartcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    
    List<Medication> findByUserOrderByCreatedAtDesc(User user);
    
    List<Medication> findByUserAndStatus(User user, MedicationStatus status);
    
    @Query("SELECT m FROM Medication m WHERE m.user = :user AND m.status = 'ACTIVE' AND " +
           "(m.endDate IS NULL OR m.endDate >= :now)")
    List<Medication> findActiveMedicationsByUser(
        @Param("user") User user,
        @Param("now") LocalDateTime now
    );
    
    @Query("SELECT m FROM Medication m WHERE m.status = 'ACTIVE' AND " +
           "m.remainingQuantity IS NOT NULL AND " +
           "m.remainingQuantity <= m.refillReminderDays")
    List<Medication> findMedicationsNeedingRefill();
}
