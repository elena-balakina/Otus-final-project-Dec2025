package org.otus.finalProject.dto.stats;

public record TopScorersStatResponse(
        Long playerId,
        String firstName,
        String lastName,
        Long teamId, // current team
        int goals
) {
}
