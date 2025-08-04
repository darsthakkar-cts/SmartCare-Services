package com.smartcare.service;

import com.smartcare.dto.healthtracking.HealthTrackingOverviewDto;
import com.smartcare.dto.healthtracking.HealthGoalDto;
import com.smartcare.model.HealthGoal;
import com.smartcare.model.HealthTracking;
import com.smartcare.model.User;
import com.smartcare.repository.HealthGoalRepository;
import com.smartcare.repository.HealthTrackingRepository;
import com.smartcare.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HealthTrackingServiceImpl implements HealthTrackingService {
    @Autowired
    private HealthTrackingRepository healthTrackingRepository;
    @Autowired
    private HealthGoalRepository healthGoalRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public HealthTrackingOverviewDto getOverview(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        HealthTracking ht = healthTrackingRepository.findByUser(user).orElseThrow();
        HealthTrackingOverviewDto dto = new HealthTrackingOverviewDto();
        dto.healthScore = ht.getHealthScore();
        dto.activity = ht.getActivity();
        dto.sleep = ht.getSleep();
        dto.nutrition = ht.getNutrition();
        dto.vitals = ht.getVitals();
        dto.goals = ht.getGoals().stream().map(this::toGoalDto).collect(Collectors.toList());
        return dto;
    }

    @Override
    public List<HealthGoalDto> getGoals(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        HealthTracking ht = healthTrackingRepository.findByUser(user).orElseThrow();
        return ht.getGoals().stream().map(this::toGoalDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HealthGoalDto addGoal(Long userId, HealthGoalDto goalDto) {
        User user = userRepository.findById(userId).orElseThrow();
        HealthTracking ht = healthTrackingRepository.findByUser(user).orElseThrow();
        HealthGoal goal = new HealthGoal();
        goal.setGoalType(goalDto.goalType);
        goal.setDescription(goalDto.description);
        goal.setTargetValue(goalDto.targetValue);
        goal.setCurrentValue(goalDto.currentValue);
        goal.setStatus(goalDto.status);
        goal.setStartDate(goalDto.startDate);
        goal.setEndDate(goalDto.endDate);
        goal.setHealthTracking(ht);
        healthGoalRepository.save(goal);
        return toGoalDto(goal);
    }

    @Override
    @Transactional
    public HealthGoalDto updateGoal(Long userId, Long goalId, HealthGoalDto goalDto) {
        HealthGoal goal = healthGoalRepository.findById(goalId).orElseThrow();
        goal.setGoalType(goalDto.goalType);
        goal.setDescription(goalDto.description);
        goal.setTargetValue(goalDto.targetValue);
        goal.setCurrentValue(goalDto.currentValue);
        goal.setStatus(goalDto.status);
        goal.setStartDate(goalDto.startDate);
        goal.setEndDate(goalDto.endDate);
        healthGoalRepository.save(goal);
        return toGoalDto(goal);
    }

    @Override
    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        healthGoalRepository.deleteById(goalId);
    }

    private HealthGoalDto toGoalDto(HealthGoal goal) {
        HealthGoalDto dto = new HealthGoalDto();
        dto.id = goal.getId();
        dto.goalType = goal.getGoalType();
        dto.description = goal.getDescription();
        dto.targetValue = goal.getTargetValue();
        dto.currentValue = goal.getCurrentValue();
        dto.status = goal.getStatus();
        dto.startDate = goal.getStartDate();
        dto.endDate = goal.getEndDate();
        return dto;
    }
}
