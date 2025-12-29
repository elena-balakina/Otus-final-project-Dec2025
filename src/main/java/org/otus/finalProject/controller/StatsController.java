package org.otus.finalProject.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.stats.PlayerStatResponse;
import org.otus.finalProject.dto.stats.TeamStatResponse;
import org.otus.finalProject.dto.stats.TopScorersStatResponse;
import org.otus.finalProject.service.base.StatsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Stats", description = "Statistics")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    // GET /api/stats/teams/{id}?year=
    @GetMapping("/teams/{id}")
    public TeamStatResponse teamStats(@PathVariable Long id,
                                      @RequestParam(required = false) Integer year) {
        return service.teamStats(id, year);
    }

    // GET /api/stats/players/{id}?year=
    @GetMapping("/players/{id}")
    public PlayerStatResponse playerStats(@PathVariable Long id,
                                          @RequestParam(required = false) Integer year) {
        return service.playerStats(id, year);
    }

    // GET /api/stats/top-teams?year=&limit=
    @GetMapping("/top-teams")
    public List<TeamStatResponse> topTeams(@RequestParam(required = false) Integer year,
                                           @RequestParam(defaultValue = "10") Integer limit) {
        return service.topTeams(year, limit);
    }

    // GET /api/stats/top-scorers?teamId=&year=&limit=
    @GetMapping("/top-scorers")
    public List<TopScorersStatResponse> topScorers(@RequestParam(required = false) Long teamId,
                                                   @RequestParam(required = false) Integer year,
                                                   @RequestParam(defaultValue = "10") Integer limit) {
        return service.topScorers(teamId, year, limit);
    }
}
