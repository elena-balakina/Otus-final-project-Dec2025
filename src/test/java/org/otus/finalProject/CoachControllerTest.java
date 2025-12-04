package org.otus.finalProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.otus.finalProject.controller.CoachController;
import org.otus.finalProject.dto.coach.CoachCreateRequest;
import org.otus.finalProject.dto.coach.CoachPatchRequest;
import org.otus.finalProject.dto.coach.CoachResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.handler.RestExceptionHandler;
import org.otus.finalProject.service.base.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(controllers = CoachController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class CoachControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CoachService coachService;

    @Nested
    @DisplayName("POST /api/coaches")
    class Create {

        @Test
        void shouldCreateAndReturn201() throws Exception {
            var req = new CoachCreateRequest("Pep", "Guardiola");
            var resp = new CoachResponse(1L, "Pep", "Guardiola", Set.of());

            Mockito.when(coachService.create(any(CoachCreateRequest.class))).thenReturn(resp);

            mockMvc.perform(post("/api/coaches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("Pep"))
                    .andExpect(jsonPath("$.lastName").value("Guardiola"));
        }

        @Test
        void shouldReturn400_whenValidationFails() throws Exception {
            // firstName is empty
            var badJson = """
                    {"firstName":"", "lastName":"Smith"}
                    """;

            mockMvc.perform(post("/api/coaches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("validation_failed"))
                    .andExpect(jsonPath("$.details", hasSize(greaterThanOrEqualTo(1))));
        }
    }

    @Nested
    @DisplayName("GET /api/coaches")
    class GetAll {

        @Test
        void shouldReturn200_withList() throws Exception {
            var r1 = new CoachResponse(1L, "Pep", "Guardiola", Set.of(10L, 20L));
            var r2 = new CoachResponse(2L, "Jurgen", "Klopp", Set.of());

            Mockito.when(coachService.findAll()).thenReturn(List.of(r1, r2));

            mockMvc.perform(get("/api/coaches"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].firstName").value("Jurgen"));
        }
    }

    @Nested
    @DisplayName("GET /api/coaches/{id}")
    class GetById {

        @Test
        void shouldReturn200_whenFound() throws Exception {
            var r = new CoachResponse(42L, "Mikel", "Arteta", Set.of(7L));

            Mockito.when(coachService.findById(42L)).thenReturn(r);

            mockMvc.perform(get("/api/coaches/{id}", 42))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.lastName").value("Arteta"));
        }

        @Test
        void shouldReturn404_whenNotFound() throws Exception {
            Mockito.when(coachService.findById(99L))
                    .thenThrow(new NotFoundException("Coach not found: 99"));

            mockMvc.perform(get("/api/coaches/{id}", 99))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"))
                    .andExpect(jsonPath("$.message").value(containsString("99")));
        }
    }

    @Nested
    @DisplayName("PATCH /api/coaches/{id}")
    class Patch {

        @Test
        void shouldPatchAndReturn200() throws Exception {
            var patch = new CoachPatchRequest(null, "G.");
            var resp = new CoachResponse(5L, "Pep", "G.", Set.of());

            Mockito.when(coachService.patch(eq(5L), any(CoachPatchRequest.class))).thenReturn(resp);

            mockMvc.perform(patch("/api/coaches/{id}", 5)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patch)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.lastName").value("G."));
        }

        @Test
        void shouldReturn400_whenPatchValidationFails() throws Exception {
            // lastName is too long
            var longLastName = "x".repeat(61);
            var badPatchJson = """
                    {"lastName": "%s"}
                    """.formatted(longLastName);

            mockMvc.perform(patch("/api/coaches/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badPatchJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("validation_failed"));
        }

        @Test
        void shouldReturn404_whenPatchNotFound() throws Exception {
            var patch = new CoachPatchRequest("Someone", null);

            Mockito.when(coachService.patch(eq(123L), any(CoachPatchRequest.class)))
                    .thenThrow(new NotFoundException("Coach not found: 123"));

            mockMvc.perform(patch("/api/coaches/{id}", 123)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patch)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/coaches/{id}")
    class DeleteById {

        @Test
        void shouldReturn204_whenDeleted() throws Exception {
            Mockito.doNothing().when(coachService).delete(7L);

            mockMvc.perform(delete("/api/coaches/{id}", 7))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404_whenDeleteNotFound() throws Exception {
            Mockito.doThrow(new NotFoundException("Coach not found: 404"))
                    .when(coachService).delete(404L);

            mockMvc.perform(delete("/api/coaches/{id}", 404))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }
}

