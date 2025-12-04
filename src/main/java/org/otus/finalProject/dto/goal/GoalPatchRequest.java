package org.otus.finalProject.dto.goal;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record GoalPatchRequest(
        @Nullable
        @Positive
        Long matchId,

        @Nullable
        @Positive
        Long playerId,

        @Nullable
        @Min(0)
        @Max(120)
        Integer goalTime
) {
}
