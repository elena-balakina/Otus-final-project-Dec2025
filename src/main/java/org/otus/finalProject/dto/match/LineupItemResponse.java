package org.otus.finalProject.dto.match;

public record LineupItemResponse(
        Long playerId,
        String firstName,
        String lastName,
        boolean isStarting,
        Integer minutesPlayed
) {
}

