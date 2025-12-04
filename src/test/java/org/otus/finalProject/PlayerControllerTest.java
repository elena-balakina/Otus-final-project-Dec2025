package org.otus.finalProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.otus.finalProject.controller.PlayerController;
import org.otus.finalProject.dto.player.PlayerCreateRequest;
import org.otus.finalProject.dto.player.PlayerPatchRequest;
import org.otus.finalProject.dto.player.PlayerResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.handler.RestExceptionHandler;
import org.otus.finalProject.service.base.PlayerService;
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
@WebMvcTest(controllers = PlayerController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class PlayerControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PlayerService playerService;

    @Nested
    @DisplayName("POST /api/players")
    class Create {

        @Test
        void shouldCreateAndReturn201() throws Exception {
            var req = new PlayerCreateRequest("Lionel", "Messi", 3L);
            var resp = new PlayerResponse(1L, "Lionel", "Messi", 3L);

            Mockito.when(playerService.create(any(PlayerCreateRequest.class))).thenReturn(resp);

            mockMvc.perform(post("/api/players")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("Lionel"))
                    .andExpect(jsonPath("$.lastName").value("Messi"))
                    .andExpect(jsonPath("$.teamId").value(3));
        }

        @Test
        void shouldReturn400_whenValidationFails() throws Exception {
            // firstName is empty
            var badJson = """
                    {"firstName":"", "lastName":"Smith", "teamId": 1}
                    """;

            mockMvc.perform(post("/api/players")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("validation_failed"))
                    .andExpect(jsonPath("$.details", hasSize(greaterThanOrEqualTo(1))));
        }
    }

    @Nested
    @DisplayName("GET /api/players")
    class GetAll {

        @Test
        void shouldReturn200_withList() throws Exception {
            var r1 = new PlayerResponse(1L, "Kylian", "Mbappe", 10L);
            var r2 = new PlayerResponse(2L, "Erling", "Haaland", null);

            Mockito.when(playerService.findAll()).thenReturn(List.of(r1, r2));

            mockMvc.perform(get("/api/players"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].teamId").value(10))
                    .andExpect(jsonPath("$[1].firstName").value("Erling"))
                    .andExpect(jsonPath("$[1].teamId").doesNotExist()); // null не сериализуется, если включён NON_NULL
        }
    }

    @Nested
    @DisplayName("GET /api/players/{id}")
    class GetById {

        @Test
        void shouldReturn200_whenFound() throws Exception {
            var r = new PlayerResponse(42L, "Kevin", "De Bruyne", 7L);

            Mockito.when(playerService.findById(42L)).thenReturn(r);

            mockMvc.perform(get("/api/players/{id}", 42))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.lastName").value("De Bruyne"))
                    .andExpect(jsonPath("$.teamId").value(7));
        }

        @Test
        void shouldReturn404_whenNotFound() throws Exception {
            Mockito.when(playerService.findById(99L))
                    .thenThrow(new NotFoundException("Player not found: 99"));

            mockMvc.perform(get("/api/players/{id}", 99))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"))
                    .andExpect(jsonPath("$.message").value(containsString("99")));
        }
    }

    @Nested
    @DisplayName("PATCH /api/players/{id}")
    class Patch {

        @Test
        void shouldPatchAndReturn200_changeLastName_andAttachTeam() throws Exception {
            var patchReq = new PlayerPatchRequest(null, "Anderson", 5L);
            var resp = new PlayerResponse(5L, "SameFirst", "Anderson", 5L);

            Mockito.when(playerService.patch(eq(5L), any(PlayerPatchRequest.class))).thenReturn(resp);

            mockMvc.perform(patch("/api/players/{id}", 5)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchReq)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.lastName").value("Anderson"))
                    .andExpect(jsonPath("$.teamId").value(5));
        }

        @Test
        void shouldPatchAndReturn200_detachTeam() throws Exception {
            var patchReq = new PlayerPatchRequest("Leo", null, null); // teamId = null → отвязать
            var resp = new PlayerResponse(10L, "Leo", "SameLast", null);

            Mockito.when(playerService.patch(eq(10L), any(PlayerPatchRequest.class))).thenReturn(resp);

            mockMvc.perform(patch("/api/players/{id}", 10)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchReq)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.firstName").value("Leo"))
                    .andExpect(jsonPath("$.teamId").doesNotExist()); // если NON_NULL включён
        }

        @Test
        void shouldReturn400_whenPatchValidationFails() throws Exception {
            // lastName is too long (assuming @Size(max=60))
            var longLastName = "x".repeat(61);
            var badPatchJson = """
                    {"lastName": "%s"}
                    """.formatted(longLastName);

            mockMvc.perform(patch("/api/players/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badPatchJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("validation_failed"));
        }

        @Test
        void shouldReturn404_whenPatchNotFound() throws Exception {
            var patchReq = new PlayerPatchRequest("Someone", null, 2L);

            Mockito.when(playerService.patch(eq(123L), any(PlayerPatchRequest.class)))
                    .thenThrow(new NotFoundException("Player not found: 123"));

            mockMvc.perform(patch("/api/players/{id}", 123)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchReq)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/players/{id}")
    class DeleteById {

        @Test
        void shouldReturn204_whenDeleted() throws Exception {
            Mockito.doNothing().when(playerService).delete(7L);

            mockMvc.perform(delete("/api/players/{id}", 7))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404_whenDeleteNotFound() throws Exception {
            Mockito.doThrow(new NotFoundException("Player not found: 404"))
                    .when(playerService).delete(404L);

            mockMvc.perform(delete("/api/players/{id}", 404))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"));
        }
    }
}
