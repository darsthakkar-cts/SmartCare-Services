package com.smartcare.repository;

import com.smartcare.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    List<Doctor> findByIsActiveTrue();
    
    Page<Doctor> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT d FROM Doctor d WHERE d.isActive = true AND " +
           "(:specialization IS NULL OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND " +
           "(:city IS NULL OR LOWER(d.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:state IS NULL OR LOWER(d.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
           "(:minRating IS NULL OR d.rating >= :minRating)")
    Page<Doctor> findDoctorsWithFilters(
        @Param("specialization") String specialization,
        @Param("city") String city,
        @Param("state") String state,
        @Param("minRating") Double minRating,
        Pageable pageable
    );
    
    @Query("SELECT d FROM Doctor d JOIN d.languages l WHERE d.isActive = true AND " +
           "LOWER(l) LIKE LOWER(CONCAT('%', :language, '%'))")
    Page<Doctor> findByLanguage(@Param("language") String language, Pageable pageable);
    
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
}
