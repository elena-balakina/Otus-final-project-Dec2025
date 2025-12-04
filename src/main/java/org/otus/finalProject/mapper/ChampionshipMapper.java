package org.otus.finalProject.mapper;

import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.championship.ChampionshipCreateRequest;
import org.otus.finalProject.dto.championship.ChampionshipPatchRequest;
import org.otus.finalProject.dto.championship.ChampionshipResponse;
import org.otus.finalProject.persistence.model.Championship;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ChampionshipMapper {

    public Championship toEntity(ChampionshipCreateRequest dto) {
        Championship championship = new Championship();
        championship.setName(dto.name());
        if (dto.startDate() != null && !dto.startDate().isBlank()) {
            championship.setStartDate(Instant.parse(dto.startDate()));
        }
        if (dto.endDate() != null && !dto.endDate().isBlank()) {
            championship.setEndDate(Instant.parse(dto.endDate()));
        }
        return championship;
    }

    public ChampionshipResponse toResponse(Championship championship) {
        return new ChampionshipResponse(
                championship.getId(),
                championship.getName(),
                championship.getStartDate() == null ? null : championship.getStartDate().toString(),
                championship.getEndDate() == null ? null : championship.getEndDate().toString());
    }

    public void applyPatch(Championship championship, ChampionshipPatchRequest dto) {
        if (dto.name() != null) championship.setName(dto.name());
        if (dto.startDate() != null) {
            championship.setStartDate(dto.startDate().isBlank() ? null : Instant.parse(dto.startDate()));
        }
        if (dto.endDate() != null) {
            championship.setEndDate(dto.endDate().isBlank() ? null : Instant.parse(dto.endDate()));
        }
    }
}
