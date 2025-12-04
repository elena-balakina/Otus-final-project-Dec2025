package org.otus.finalProject.dto.team;

import jakarta.annotation.Nullable;
import org.otus.finalProject.dto.player.PlayerShortResponse;

import java.util.Set;

public record TeamResponse(
        Long id,

        String name,

        String country,

        @Nullable
        Long coachId,

        @Nullable
        Set<PlayerShortResponse> players
) {
}
