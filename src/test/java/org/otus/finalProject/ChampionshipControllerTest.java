package org.otus.finalProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.otus.finalProject.controller.ChampionshipController;
import org.otus.finalProject.dto.championship.ChampionshipCreateRequest;
import org.otus.finalProject.dto.championship.ChampionshipPatchRequest;
import org.otus.finalProject.dto.championship.ChampionshipResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.handler.RestExceptionHandler;
import org.otus.finalProject.service.base.ChampionshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(controllers = ChampionshipController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class ChampionshipControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ChampionshipService championshipService;

    @Nested
    @DisplayName("POST /api/championships")
    class Create {

        @Test
        void shouldCreateAndReturn201() throws Exception {
            var request = new ChampionshipCreateRequest(
                    "Premier League",
                    "2025-08-10T12:00:00Z",
                    null);

            var response = new ChampionshipResponse(
                    1L,
                    "Premier League",
                    "2025-08-10T12:00:00Z",
                    null);

            Mockito.when(championshipService.create(any(ChampionshipCreateRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/championships")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Premier League"))
                    .andExpect(jsonPath("$.startDate").value("2025-08-10T12:00:00Z"));
        }

        @Test
        void shouldReturn400_whenValidationFails() throws Exception {
            // name is empty
            var badJson = """
                    {"name":"", "startDate":"2025-08-10T12:00:00Z"}
                    """;

            mockMvc.perform(post("/api/championships")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("validation_failed"))
                    .andExpect(jsonPath("$.details", hasSize(greaterThanOrEqualTo(1))));
        }


        @Test
        void shouldReturn400_whenNameDuplicate() throws Exception {
            var request = new ChampionshipCreateRequest(
                    "Premier League",
                    "2025-08-10T12:00:00Z",
                    null);

            Mockito.when(championshipService.create(any(ChampionshipCreateRequest.class)))
                    .thenThrow(new IllegalArgumentException("Championship with this name already exists"));

            mockMvc.perform(post("/api/championships")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error",
                            anyOf(equalTo("bad_request"), equalTo("invalid_argument"))))
                    .andExpect(jsonPath("$.message",
                            containsStringIgnoringCase("already exists")));
        }
    }

    @Nested
    @DisplayName("GET /api/championships")
    class GetAll {

        @Test
        void shouldReturn200_withList() throws Exception {
            var response1 = new ChampionshipResponse(
                    1L,
                    "Premier League",
                    null,
                    null);
            var response2 = new ChampionshipResponse(
                    2L,
                    "La Liga",
                    null,
                    null);

            Mockito.when(championshipService.findAll()).thenReturn(List.of(response1, response2));

            mockMvc.perform(get("/api/championships"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value("Premier League"))
                    .andExpect(jsonPath("$[1].name").value("La Liga"));
        }
    }

    @Nested
    @DisplayName("GET /api/championships/{id}")
    class GetById {

        @Test
        void shouldReturn200_whenFound() throws Exception {
            var response = new ChampionshipResponse(
                    42L,
                    "Serie A",
                    null,
                    "2026-05-25T18:00:00Z");

            Mockito.when(championshipService.findById(42L)).thenReturn(response);

            mockMvc.perform(get("/api/championships/{id}", 42))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.name").value("Serie A"))
                    .andExpect(jsonPath("$.endDate").value("2026-05-25T18:00:00Z"));
        }

        @Test
        void shouldReturn404_whenNotFound() throws Exception {
            Mockito.when(championshipService.findById(99L))
                    .thenThrow(new NotFoundException("Championship not found: 99"));

            mockMvc.perform(get("/api/championships/{id}", 99))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"))
                    .andExpect(jsonPath("$.message", containsString("99")));
        }
    }

    @Nested
    @DisplayName("PATCH /api/championships/{id}")
    class Patch {

        @Test
        void shouldPatchAndReturn200() throws Exception {
            var patch = new ChampionshipPatchRequest(
                    "Premier League",
                    null,
                    "2026-05-25T18:00:00Z");

            var response = new ChampionshipResponse(
                    5L,
                    "Premier League",
                    null,
                    "2026-05-25T18:00:00Z");

            Mockito.when(championshipService.patch(eq(5L), any(ChampionshipPatchRequest.class))).thenReturn(response);

            mockMvc.perform(patch("/api/championships/{id}", 5)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patch)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.endDate").value("2026-05-25T18:00:00Z"));
        }

        @Test
        void shouldReturn400_whenPatchValidationFails() throws Exception {
            // name too long ( > 100 )
            var longName = "x".repeat(101);
            var badPatchJson = """
                    {"name":"%s"}
                    """.formatted(longName);

            mockMvc.perform(patch("/api/championships/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badPatchJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("validation_failed"));
        }

        @Test
        void shouldReturn404_whenPatchNotFound() throws Exception {
            var patch = new ChampionshipPatchRequest(
                    "Some League",
                    null,
                    null);

            Mockito.when(championshipService.patch(eq(123L), any(ChampionshipPatchRequest.class)))
                    .thenThrow(new NotFoundException("Championship not found: 123"));

            mockMvc.perform(patch("/api/championships/{id}", 123)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patch)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/championships/{id}")
    class DeleteById {

        @Test
        void shouldReturn204_whenDeleted() throws Exception {
            Mockito.doNothing().when(championshipService).delete(7L);

            mockMvc.perform(delete("/api/championships/{id}", 7))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404_whenDeleteNotFound() throws Exception {
            Mockito.doThrow(new NotFoundException("Championship not found: 404"))
                    .when(championshipService).delete(404L);

            mockMvc.perform(delete("/api/championships/{id}", 404))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }
}
