package org.otus.finalProject.dto.coach;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CoachCreateRequest(
        @NotBlank
        @Size(max = 60)
        String firstName,

        @NotBlank
        @Size(max = 60)
        String lastName
) {
}
