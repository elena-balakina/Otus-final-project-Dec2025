package org.otus.finalProject.dto.coach;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;


public record CoachPatchRequest(
        @Nullable
        @Size(max = 60)
        String firstName,

        @Nullable
        @Size(max = 60)
        String lastName
) {
}