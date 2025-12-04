package org.otus.finalProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.otus.finalProject.controller.GoalController;
import org.otus.finalProject.dto.goal.GoalCreateRequest;
import org.otus.finalProject.dto.goal.GoalPatchRequest;
import org.otus.finalProject.dto.goal.GoalResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.handler.RestExceptionHandler;
import org.otus.finalProject.service.base.GoalService;
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
@WebMvcTest(controllers = GoalController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class GoalControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    GoalService goalService;

    @Nested
    @DisplayName("POST /api/goals")
    class Create {

        @Test
        void shouldCreateAndReturn201() throws Exception {
            var request = new GoalCreateRequest(5L, 12L, 33);
            var response = new GoalResponse(100L, 5L, 12L, 33);

            Mockito.when(goalService.create(any(GoalCreateRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/goals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(100))
                    .andExpect(jsonPath("$.matchId").value(5))
                    .andExpect(jsonPath("$.playerId").value(12))
                    .andExpect(jsonPath("$.goalTime").value(33));
        }

        @Test
        void shouldReturn400_whenValidationFails() throws Exception {
            // no matchId
            var badJson = """
                        {"playerId":12,"goalTime":15}
                    """;

            mockMvc.perform(post("/api/goals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("validation_failed"))
                    .andExpect(jsonPath("$.details", hasSize(greaterThanOrEqualTo(1))));
        }
    }

    @Nested
    @DisplayName("GET /api/goals")
    class GetAll {

        @Test
        void shouldReturn200_withList() throws Exception {
            var response1 = new GoalResponse(1L, 5L, 12L, 10);
            var response2 = new GoalResponse(2L, 6L, 13L, 77);

            Mockito.when(goalService.findAll()).thenReturn(List.of(response1, response2));

            mockMvc.perform(get("/api/goals"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].goalTime").value(77));
        }
    }

    @Nested
    @DisplayName("GET /api/goals/{id}")
    class GetById {

        @Test
        void shouldReturn200_whenFound() throws Exception {
            var response = new GoalResponse(42L, 7L, 9L, 55);

            Mockito.when(goalService.findById(42L)).thenReturn(response);

            mockMvc.perform(get("/api/goals/{id}", 42))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.goalTime").value(55));
        }

        @Test
        void shouldReturn404_whenNotFound() throws Exception {
            Mockito.when(goalService.findById(404L))
                    .thenThrow(new NotFoundException("Goal not found: 404"));

            mockMvc.perform(get("/api/goals/{id}", 404))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"))
                    .andExpect(jsonPath("$.message", containsString("404")));
        }
    }

    @Nested
    @DisplayName("GET /api/goals/player/{playerId}")
    class GetByPlayer {

        @Test
        void shouldReturn200_withGoalsList() throws Exception {
            var response1 = new GoalResponse(10L, 5L, 12L, 15);
            var response2 = new GoalResponse(11L, 8L, 12L, 60);

            Mockito.when(goalService.findByPlayer(12L)).thenReturn(List.of(response1, response2));

            mockMvc.perform(get("/api/goals/player/{playerId}", 12))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].playerId").value(12))
                    .andExpect(jsonPath("$[1].goalTime").value(60));
        }

        @Test
        void shouldReturn200_withEmptyList_whenNoGoals() throws Exception {
            Mockito.when(goalService.findByPlayer(77L)).thenReturn(List.of());

            mockMvc.perform(get("/api/goals/player/{playerId}", 77))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("PATCH /api/goals/{id}")
    class Patch {

        @Test
        void shouldPatchAndReturn200() throws Exception {
            var patch = new GoalPatchRequest(null, null, 88);
            var response = new GoalResponse(5L, 1L, 2L, 88);

            Mockito.when(goalService.patch(eq(5L), any(GoalPatchRequest.class))).thenReturn(response);

            mockMvc.perform(patch("/api/goals/{id}", 5)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patch)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.goalTime").value(88));
        }

        @Test
        void shouldReturn400_whenPatchValidationFails() throws Exception {
            // goalTime > 120
            var badPatchJson = """
                        {"goalTime": 999}
                    """;

            mockMvc.perform(patch("/api/goals/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badPatchJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn404_whenPatchNotFound() throws Exception {
            var patch = new GoalPatchRequest(null, null, 70);

            Mockito.when(goalService.patch(eq(123L), any(GoalPatchRequest.class)))
                    .thenThrow(new NotFoundException("Goal not found: 123"));

            mockMvc.perform(patch("/api/goals/{id}", 123)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patch)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/goals/{id}")
    class DeleteById {

        @Test
        void shouldReturn204_whenDeleted() throws Exception {
            Mockito.doNothing().when(goalService).delete(7L);

            mockMvc.perform(delete("/api/goals/{id}", 7))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404_whenDeleteNotFound() throws Exception {
            Mockito.doThrow(new NotFoundException("Goal not found: 404"))
                    .when(goalService).delete(404L);

            mockMvc.perform(delete("/api/goals/{id}", 404))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }
}
