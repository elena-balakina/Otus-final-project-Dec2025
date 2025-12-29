package org.otus.finalProject.service.base;

import org.otus.finalProject.dto.stats.PlayerStatResponse;
import org.otus.finalProject.dto.stats.TeamStatResponse;
import org.otus.finalProject.dto.stats.TopScorersStatResponse;

import java.util.List;

public interface StatsPublisher {

    void sendTeamStats(TeamStatResponse payload);

    void sendPlayerStats(PlayerStatResponse payload);

    void sendTopTeams(List<TeamStatResponse> payload, Integer year, Integer limit);

    void sendTopScorers(List<TopScorersStatResponse> payload, Long teamId, Integer year, Integer limit);
}
