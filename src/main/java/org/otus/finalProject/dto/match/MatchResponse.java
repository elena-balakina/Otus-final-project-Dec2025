package org.otus.finalProject.dto.match;

import java.time.Instant;
import java.util.List;

public record MatchResponse(
        Long id,
        Long team1Id,
        Long team2Id,
        int team1Score,
        int team2Score,
        Instant matchDate,
        Long championshipId,
        List<LineupItemResponse> lineupTeam1,
        List<LineupItemResponse> lineupTeam2
) {
}

