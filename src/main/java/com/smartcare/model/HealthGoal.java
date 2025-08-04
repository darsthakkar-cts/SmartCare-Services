package com.smartcare.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class HealthGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_tracking_id")
    private HealthTracking healthTracking;

    private String goalType; // e.g., "Step", "Weight", "Sleep", "Meditation"
    private String description;
    private int targetValue;
    private int currentValue;
    private String status; // e.g., "IN_PROGRESS", "ON_TRACK", "COMPLETED"
    private LocalDate startDate;
    private LocalDate endDate;

    // getters and setters
}
