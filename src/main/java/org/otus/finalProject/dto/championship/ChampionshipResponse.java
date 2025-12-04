package org.otus.finalProject.dto.championship;

public record ChampionshipResponse(
        Long id,

        String name,

        String startDate,

        String endDate
) {
}
