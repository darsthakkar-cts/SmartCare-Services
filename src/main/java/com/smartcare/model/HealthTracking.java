package com.smartcare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int healthScore;
    private int activity;
    private int sleep;
    private int nutrition;
    private int vitals;

    @OneToMany(mappedBy = "healthTracking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthGoal> goals;

    private LocalDate lastUpdated;
}
