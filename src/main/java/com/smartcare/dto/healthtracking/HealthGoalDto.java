package com.smartcare.dto.healthtracking;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Health Goal DTO")
public class HealthGoalDto {
    public Long id;
    @NotNull
    public String goalType;
    public String description;
    public int targetValue;
    public int currentValue;
    public String status;
    public LocalDate startDate;
    public LocalDate endDate;
}
