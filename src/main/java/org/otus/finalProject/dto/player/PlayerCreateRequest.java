package org.otus.finalProject.dto.player;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PlayerCreateRequest(
        @NotBlank
        @Size(max = 60)
        String firstName,

        @NotBlank
        @Size(max = 60)
        String lastName,

        @Nullable
        @Positive
        Long teamId
) {
}

