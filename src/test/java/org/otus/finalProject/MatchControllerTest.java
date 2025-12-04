package org.otus.finalProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.otus.finalProject.controller.MatchController;
import org.otus.finalProject.dto.match.MatchCreateRequest;
import org.otus.finalProject.dto.match.MatchPatchRequest;
import org.otus.finalProject.dto.match.MatchResponse;
import org.otus.finalProject.dto.match.MatchResultRequest;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.handler.RestExceptionHandler;
import org.otus.finalProject.service.base.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(controllers = MatchController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class MatchControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MatchService matchService;

    @Nested
    @DisplayName("POST /api/matches")
    class Create {

        @Test
        void shouldCreateAndReturn201() throws Exception {
            var when = Instant.parse("2025-10-25T18:00:00Z");
            var request = new MatchCreateRequest(1L, null, 2L, null, when, 7L);
            var response = new MatchResponse(10L, 1L, 2L, 0, 0, when, 7L, null, null);

            Mockito.when(matchService.create(any(MatchCreateRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/matches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.team1Id").value(1))
                    .andExpect(jsonPath("$.team2Id").value(2))
                    .andExpect(jsonPath("$.team1Score").value(0))
                    .andExpect(jsonPath("$.team2Score").value(0))
                    .andExpect(jsonPath("$.championshipId").value(7));
        }

        @Test
        void shouldReturn400_whenValidationFails() throws Exception {
            // no team1Id
            var badJson = """
                        {"team2Id": 2, "matchDate":"2025-10-25T18:00:00Z"}
                    """;

            mockMvc.perform(post("/api/matches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("validation_failed"))
                    .andExpect(jsonPath("$.details", hasSize(greaterThanOrEqualTo(1))));
        }
    }

    @Nested
    @DisplayName("GET /api/matches")
    class GetAll {

        @Test
        void shouldReturn200_withList() throws Exception {
            var time1 = Instant.parse("2025-10-25T18:00:00Z");
            var time2 = Instant.parse("2025-10-26T18:00:00Z");
            var response1 = new MatchResponse(1L, 1L, 2L, 1, 1, time1, 5L, null, null);
            var response2 = new MatchResponse(2L, 3L, 4L, 2, 0, time2, null, null, null);

            Mockito.when(matchService.findAll()).thenReturn(List.of(response1, response2));

            mockMvc.perform(get("/api/matches"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].id").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/matches/{id}")
    class GetById {

        @Test
        void shouldReturn200_whenFound() throws Exception {
            var when = Instant.parse("2025-10-27T12:00:00Z");
            var response = new MatchResponse(42L, 10L, 20L, 3, 2, when, 9L, null, null);

            Mockito.when(matchService.findById(42L)).thenReturn(response);

            mockMvc.perform(get("/api/matches/{id}", 42))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.team1Score").value(3))
                    .andExpect(jsonPath("$.team2Score").value(2));
        }

        @Test
        void shouldReturn404_whenNotFound() throws Exception {
            Mockito.when(matchService.findById(99L))
                    .thenThrow(new NotFoundException("Match not found: 99"));

            mockMvc.perform(get("/api/matches/{id}", 99))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"))
                    .andExpect(jsonPath("$.message", containsString("99")));
        }
    }

    @Nested
    @DisplayName("PATCH /api/matches/{id}")
    class Patch {

        @Test
        void shouldPatchAndReturn200() throws Exception {
            var when = Instant.parse("2025-10-28T18:00:00Z");
            var patch = new MatchPatchRequest(null, null, null, null, when, null);
            var response = new MatchResponse(5L, 1L, 2L, 0, 0, when, 7L, null, null);

            Mockito.when(matchService.patch(eq(5L), any(MatchPatchRequest.class))).thenReturn(response);

            mockMvc.perform(patch("/api/matches/{id}", 5)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patch)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.matchDate").exists());
        }

        @Test
        void shouldReturn400_whenPatchValidationFails() throws Exception {
            // invalid date format —> 400 bad_request/validation_failed
            var badPatchJson = """
                        {"matchDate": "not-a-date"}
                    """;

            mockMvc.perform(patch("/api/matches/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badPatchJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn404_whenPatchNotFound() throws Exception {
            var when = Instant.parse("2025-10-29T18:00:00Z");
            var patch = new MatchPatchRequest(null, null, null, null, when, null);

            Mockito.when(matchService.patch(eq(123L), any(MatchPatchRequest.class)))
                    .thenThrow(new NotFoundException("Match not found: 123"));

            mockMvc.perform(patch("/api/matches/{id}", 123)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patch)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }

    @Nested
    @DisplayName("POST /api/matches/{id}/result")
    class SetResult {

        @Test
        void shouldSetResultAndReturn200() throws Exception {
            var when = Instant.parse("2025-10-30T18:00:00Z");
            var request = new MatchResultRequest(4, 1);
            var response = new MatchResponse(11L, 7L, 8L, 4, 1, when, null, null, null);

            Mockito.when(matchService.setResult(eq(11L), any(MatchResultRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/matches/{id}/result", 11)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(11))
                    .andExpect(jsonPath("$.team1Score").value(4))
                    .andExpect(jsonPath("$.team2Score").value(1));
        }

        @Test
        void shouldReturn400_whenScoresInvalid() throws Exception {
            // score < 0
            var badRequest = new MatchResultRequest(-1, 2);

            mockMvc.perform(post("/api/matches/{id}/result", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", anyOf(equalTo("validation_failed"), equalTo("bad_request"))));
        }
    }

    @Nested
    @DisplayName("DELETE /api/matches/{id}")
    class DeleteById {

        @Test
        void shouldReturn204_whenDeleted() throws Exception {
            Mockito.doNothing().when(matchService).delete(7L);

            mockMvc.perform(delete("/api/matches/{id}", 7))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404_whenDeleteNotFound() throws Exception {
            Mockito.doThrow(new NotFoundException("Match not found: 404"))
                    .when(matchService).delete(404L);

            mockMvc.perform(delete("/api/matches/{id}", 404))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }
}
