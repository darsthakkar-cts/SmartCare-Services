package com.smartcare.repository;

import com.smartcare.model.HealthTracking;
import com.smartcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HealthTrackingRepository extends JpaRepository<HealthTracking, Long> {
    Optional<HealthTracking> findByUser(User user);
}
