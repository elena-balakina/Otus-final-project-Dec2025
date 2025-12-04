package org.otus.finalProject.dto.goal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GoalCreateRequest(
        @NotNull
        @Positive
        Long matchId,

        @NotNull
        @Positive
        Long playerId,

        @Min(0)
        @Max(120)
        int goalTime
) {
}
