package org.otus.finalProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.otus.finalProject.controller.StatsController;
import org.otus.finalProject.dto.stats.PlayerStatResponse;
import org.otus.finalProject.dto.stats.TeamStatResponse;
import org.otus.finalProject.dto.stats.TopScorersStatResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.handler.RestExceptionHandler;
import org.otus.finalProject.service.base.StatsService;
import org.otus.finalProject.service.kafka.StatsKafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(controllers = StatsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class StatsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    StatsService statsService;

    @MockBean
    StatsKafkaProducer statsKafkaProducer;

    @Nested
    @DisplayName("GET /api/stats/teams/{id}")
    class TeamStats {

        @Test
        void shouldReturn200_withStats() throws Exception {
            var response = new TeamStatResponse(1L, 2025, 30, 20, 5, 5);
            Mockito.when(statsService.teamStats(1L, 2025)).thenReturn(response);

            mockMvc.perform(get("/api/stats/teams/{id}", 1)
                            .param("year", "2025"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.teamId").value(1))
                    .andExpect(jsonPath("$.year").value(2025))
                    .andExpect(jsonPath("$.wins").value(20))
                    .andExpect(jsonPath("$.losses").value(5));

            Mockito.verify(statsKafkaProducer).sendTeamStats(response);
        }

        @Test
        void shouldReturn404_whenTeamNotFound() throws Exception {
            Mockito.when(statsService.teamStats(99L, null))
                    .thenThrow(new NotFoundException("Team not found: 99"));

            mockMvc.perform(get("/api/stats/teams/{id}", 99))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"))
                    .andExpect(jsonPath("$.message", containsString("99")));
        }
    }

    @Nested
    @DisplayName("GET /api/stats/players/{id}")
    class PlayerStats {

        @Test
        void shouldReturn200_withStats() throws Exception {
            var response = new PlayerStatResponse(7L, 2024, 25, 12, 0.48);
            Mockito.when(statsService.playerStats(7L, 2024)).thenReturn(response);

            mockMvc.perform(get("/api/stats/players/{id}", 7)
                            .param("year", "2024"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.playerId").value(7))
                    .andExpect(jsonPath("$.matchesPlayed").value(25))
                    .andExpect(jsonPath("$.goals").value(12))
                    .andExpect(jsonPath("$.avgGoalsPerMatch").value(closeTo(0.48, 0.001)));

            Mockito.verify(statsKafkaProducer).sendPlayerStats(response);
        }

        @Test
        void shouldReturn404_whenPlayerNotFound() throws Exception {
            Mockito.when(statsService.playerStats(404L, null))
                    .thenThrow(new NotFoundException("Player not found: 404"));

            mockMvc.perform(get("/api/stats/players/{id}", 404))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("not_found"))
                    .andExpect(jsonPath("$.message", containsString("404")));
        }
    }

    @Nested
    @DisplayName("GET /api/stats/top-teams")
    class TopTeams {

        @Test
        void shouldReturn200_withList() throws Exception {
            var response1 = new TeamStatResponse(1L, 2025, 38, 24, 10, 4);
            var response2 = new TeamStatResponse(2L, 2025, 38, 20, 12, 6);
            Mockito.when(statsService.topTeams(2025, 5)).thenReturn(List.of(response1, response2));

            mockMvc.perform(get("/api/stats/top-teams")
                            .param("year", "2025")
                            .param("limit", "5"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].teamId").value(1))
                    .andExpect(jsonPath("$[1].wins").value(20));

            Mockito.verify(statsKafkaProducer).sendTopTeams(List.of(response1, response2), 2025, 5);
        }
    }

    @Nested
    @DisplayName("GET /api/stats/top-scorers")
    class TopScorers {

        @Test
        void shouldReturn200_withList() throws Exception {
            var response1 = new TopScorersStatResponse(10L, "Erling", "Haaland", 1L, 36);
            var response2 = new TopScorersStatResponse(11L, "Kylian", "Mbappe", 2L, 30);

            Mockito.when(statsService.topScorers(null, 2025, 10)).thenReturn(List.of(response1, response2));

            mockMvc.perform(get("/api/stats/top-scorers")
                            .param("year", "2025")
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].firstName").value("Erling"))
                    .andExpect(jsonPath("$[1].goals").value(30));

            Mockito.verify(statsKafkaProducer).sendTopScorers(List.of(response1, response2), null, 2025, 10);
        }

        @Test
        void shouldReturn200_withFilterByTeam() throws Exception {
            var response = new TopScorersStatResponse(9L, "Harry", "Kane", 3L, 18);
            Mockito.when(statsService.topScorers(3L, 2024, 5)).thenReturn(List.of(response));

            mockMvc.perform(get("/api/stats/top-scorers")
                            .param("teamId", "3")
                            .param("year", "2024")
                            .param("limit", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].teamId").value(3))
                    .andExpect(jsonPath("$[0].goals").value(18));

            Mockito.verify(statsKafkaProducer).sendTopScorers(List.of(response), 3L, 2024, 5);
        }
    }
}
