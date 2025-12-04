package org.otus.finalProject.dto.stats;

public record TeamStatResponse(
        Long teamId,
        Integer year, // null = all years
        int played,
        int wins,
        int draws,
        int losses
) {
}
