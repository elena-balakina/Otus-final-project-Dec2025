package org.otus.finalProject.dto.match;

import jakarta.validation.constraints.Min;

public record MatchResultRequest(
        @Min(value = 0, message = "team1Score must be >= 0")
        int team1Score,

        @Min(value = 0, message = "team2Score must be >= 0")
        int team2Score
) {
}

