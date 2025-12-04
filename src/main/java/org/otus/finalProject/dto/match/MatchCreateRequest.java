package org.otus.finalProject.dto.match;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record MatchCreateRequest(
        @NotNull
        Long team1Id,

        @Nullable
        List<LineupItem> lineupTeam1,

        @NotNull
        Long team2Id,

        @Nullable
        List<LineupItem> lineupTeam2,

        @NotNull
        Instant matchDate,

        @Nullable
        Long championshipId
) {
}

