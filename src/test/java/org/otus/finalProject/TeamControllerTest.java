package org.otus.finalProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.otus.finalProject.controller.TeamController;
import org.otus.finalProject.dto.player.PlayerShortResponse;
import org.otus.finalProject.dto.team.TeamCreateRequest;
import org.otus.finalProject.dto.team.TeamPatchRequest;
import org.otus.finalProject.dto.team.TeamResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.handler.RestExceptionHandler;
import org.otus.finalProject.service.base.TeamService;
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
@WebMvcTest(controllers = TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class TeamControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TeamService teamService;

    @Nested
    @DisplayName("POST /api/teams")
    class Create {

        @Test
        void shouldCreateAndReturn201() throws Exception {
            var req = new TeamCreateRequest("Manchester City", "England", 10L, Set.of(1L, 2L));
            var players = Set.of(
                    new PlayerShortResponse(1L, "Erling", "Haaland"),
                    new PlayerShortResponse(2L, "Kevin", "De Bruyne")
            );
            var resp = new TeamResponse(1L, "Manchester City", "England", 10L, players);

            Mockito.when(teamService.create(any(TeamCreateRequest.class))).thenReturn(resp);

            mockMvc.perform(post("/api/teams")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Manchester City"))
                    .andExpect(jsonPath("$.coachId").value(10))
                    .andExpect(jsonPath("$.players", hasSize(2)))
                    .andExpect(jsonPath("$.players[*].firstName", containsInAnyOrder("Erling", "Kevin")));
        }

        @Test
        void shouldReturn400_whenValidationFails() throws Exception {
            // name is empty
            var badJson = """
                    {"name":"", "country":"England"}
                    """;

            mockMvc.perform(post("/api/teams")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("validation_failed"))
                    .andExpect(jsonPath("$.details", hasSize(greaterThanOrEqualTo(1))));
        }
    }

    @Nested
    @DisplayName("GET /api/teams")
    class GetAll {

        @Test
        void shouldReturn200_withList() throws Exception {
            var r1 = new TeamResponse(1L, "Manchester City", "England", 10L, Set.of());
            var r2 = new TeamResponse(2L, "Liverpool", "England", 11L, Set.of());

            Mockito.when(teamService.findAll()).thenReturn(List.of(r1, r2));

            mockMvc.perform(get("/api/teams"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value("Manchester City"))
                    .andExpect(jsonPath("$[1].name").value("Liverpool"));
        }
    }

    @Nested
    @DisplayName("GET /api/teams/{id}")
    class GetById {

        @Test
        void shouldReturn200_whenFound() throws Exception {
            var players = Set.of(new PlayerShortResponse(7L, "Phil", "Foden"));
            var resp = new TeamResponse(42L, "Arsenal", "England", 5L, players);

            Mockito.when(teamService.findById(42L)).thenReturn(resp);

            mockMvc.perform(get("/api/teams/{id}", 42))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.name").value("Arsenal"))
                    .andExpect(jsonPath("$.players[0].lastName").value("Foden"));
        }

        @Test
        void shouldReturn404_whenNotFound() throws Exception {
            Mockito.when(teamService.findById(99L))
                    .thenThrow(new NotFoundException("Team not found: 99"));

            mockMvc.perform(get("/api/teams/{id}", 99))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"))
                    .andExpect(jsonPath("$.message").value(containsString("99")));
        }
    }

    @Nested
    @DisplayName("PATCH /api/teams/{id}")
    class Patch {

        @Test
        void shouldPatchAndReturn200() throws Exception {
            var patch = new TeamPatchRequest("Man City", null, null, null);
            var resp = new TeamResponse(5L, "Man City", "England", 10L, Set.of());

            Mockito.when(teamService.patch(eq(5L), any(TeamPatchRequest.class))).thenReturn(resp);

            mockMvc.perform(patch("/api/teams/{id}", 5)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patch)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.name").value("Man City"));
        }

        @Test
        void shouldReturn400_whenPatchValidationFails() throws Exception {
            // name too long
            var longName = "x".repeat(61);
            var badPatchJson = """
                    {"name": "%s"}
                    """.formatted(longName);

            mockMvc.perform(patch("/api/teams/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badPatchJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("validation_failed"));
        }

        @Test
        void shouldReturn404_whenPatchNotFound() throws Exception {
            var patch = new TeamPatchRequest("Chelsea", null, null, null);

            Mockito.when(teamService.patch(eq(123L), any(TeamPatchRequest.class)))
                    .thenThrow(new NotFoundException("Team not found: 123"));

            mockMvc.perform(patch("/api/teams/{id}", 123)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patch)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/teams/{id}")
    class DeleteById {

        @Test
        void shouldReturn204_whenDeleted() throws Exception {
            Mockito.doNothing().when(teamService).delete(7L);

            mockMvc.perform(delete("/api/teams/{id}", 7))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404_whenDeleteNotFound() throws Exception {
            Mockito.doThrow(new NotFoundException("Team not found: 404"))
                    .when(teamService).delete(404L);

            mockMvc.perform(delete("/api/teams/{id}", 404))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }
}
