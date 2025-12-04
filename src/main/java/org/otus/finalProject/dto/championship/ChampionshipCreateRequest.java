package org.otus.finalProject.dto.championship;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChampionshipCreateRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Nullable
        String startDate,

        @Nullable
        String endDate
) {
}
