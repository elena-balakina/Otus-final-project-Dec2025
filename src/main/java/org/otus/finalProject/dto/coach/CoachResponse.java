package org.otus.finalProject.dto.coach;

import jakarta.annotation.Nullable;

import java.util.Set;

public record CoachResponse(
        Long id,

        String firstName,

        String lastName,

        @Nullable
        Set<Long> teamsId
) {
}
