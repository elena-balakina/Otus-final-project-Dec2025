package org.otus.finalProject.dto.goal;

public record GoalResponse(
        Long id,
        Long matchId,
        Long playerId,
        int goalTime
) {
}
