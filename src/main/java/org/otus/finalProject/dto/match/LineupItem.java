package org.otus.finalProject.dto.match;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record LineupItem(
        @NotNull
        Long playerId,

        @Nullable
        Boolean isStarting,

        @Nullable
        Integer minutesPlayed
) {
}

