package org.otus.finalProject.service.base;

import org.otus.finalProject.dto.stats.PlayerStatResponse;
import org.otus.finalProject.dto.stats.TeamStatResponse;
import org.otus.finalProject.dto.stats.TopScorersStatResponse;

import java.util.List;


public interface StatsService {
    TeamStatResponse teamStats(Long teamId, Integer year);

    PlayerStatResponse playerStats(Long playerId, Integer year);

    List<TeamStatResponse> topTeams(Integer year, Integer limit);

    List<TopScorersStatResponse> topScorers(Long teamId, Integer year, Integer limit);
}
