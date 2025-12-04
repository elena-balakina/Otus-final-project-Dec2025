package org.otus.finalProject.dto.player;

import jakarta.annotation.Nullable;

public record PlayerResponse(
        Long id,

        String firstName,

        String lastName,

        @Nullable
        Long teamId
) {
}
