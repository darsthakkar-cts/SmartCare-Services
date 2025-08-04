package com.smartcare.service;

import com.smartcare.dto.healthtracking.HealthTrackingOverviewDto;
import com.smartcare.dto.healthtracking.HealthGoalDto;
import java.util.List;

public interface HealthTrackingService {
    HealthTrackingOverviewDto getOverview(Long userId);
    List<HealthGoalDto> getGoals(Long userId);
    HealthGoalDto addGoal(Long userId, HealthGoalDto goalDto);
    HealthGoalDto updateGoal(Long userId, Long goalId, HealthGoalDto goalDto);
    void deleteGoal(Long userId, Long goalId);
}
