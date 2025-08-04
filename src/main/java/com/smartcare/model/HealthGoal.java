package com.smartcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_tracking_id")
    private HealthTracking healthTracking;

    @NotNull
    private String goalType; // e.g., "Step", "Weight", "Sleep", "Meditation"
    private String description;
    private int targetValue;
    private int currentValue;
    private String status; // e.g., "IN_PROGRESS", "ON_TRACK", "COMPLETED"
    private LocalDate startDate;
    private LocalDate endDate;
}
