package org.otus.finalProject.dto.team;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record TeamCreateRequest(
        @NotBlank
        @Size(max = 60)
        String name,

        @NotBlank
        @Size(max = 60)
        String country,

        @Nullable
        @Positive
        Long coachId,

        @Nullable
        Set<Long> playerIds
) {
}
