package org.otus.finalProject.dto.player;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public record PlayerPatchRequest(
        @Nullable
        @Size(max = 60)
        String firstName,

        @Nullable
        @Size(max = 60)
        String lastName,

        @Nullable
        @Positive
        Long teamId
) {
}