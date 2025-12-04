package org.otus.finalProject.dto.team;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record TeamPatchRequest(
        @Nullable
        @Size(max = 60)
        String name,

        @Nullable
        @Size(max = 60)
        String country,

        @Nullable
        @Positive
        Long coachId,

        @Nullable
        Set<Long> playerIds
) {
}
