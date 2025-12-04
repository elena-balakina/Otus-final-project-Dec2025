package org.otus.finalProject.dto.stats;

public record PlayerStatResponse(
        Long playerId,
        Integer year, // null = all years
        int matchesPlayed,
        int goals,
        double avgGoalsPerMatch
) {
}
