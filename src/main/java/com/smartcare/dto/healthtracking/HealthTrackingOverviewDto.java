package com.smartcare.dto.healthtracking;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Health Tracking Overview Response")
public class HealthTrackingOverviewDto {
    @Schema(description = "Overall health score")
    public int healthScore;
    @Schema(description = "Activity progress (%)")
    public int activity;
    @Schema(description = "Sleep progress (%)")
    public int sleep;
    @Schema(description = "Nutrition progress (%)")
    public int nutrition;
    @Schema(description = "Vitals progress (%)")
    public int vitals;
    @Schema(description = "List of health goals")
    public List<HealthGoalDto> goals;
}
