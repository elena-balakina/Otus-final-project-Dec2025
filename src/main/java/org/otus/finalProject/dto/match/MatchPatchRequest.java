package org.otus.finalProject.dto.match;

import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;

public record MatchPatchRequest(
        @Nullable
        Long team1Id,

        @Nullable
        List<LineupItem> lineupTeam1,

        @Nullable
        Long team2Id,

        @Nullable
        List<LineupItem> lineupTeam2,

        @Nullable
        Instant matchDate,

        @Nullable
        Long championshipId
) {
}

