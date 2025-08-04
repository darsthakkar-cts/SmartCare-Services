package com.smartcare.repository;

import com.smartcare.model.HealthGoal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthGoalRepository extends JpaRepository<HealthGoal, Long> {
}
