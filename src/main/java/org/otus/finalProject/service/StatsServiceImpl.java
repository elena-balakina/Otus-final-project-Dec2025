package org.otus.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.stats.PlayerStatResponse;
import org.otus.finalProject.dto.stats.TeamStatResponse;
import org.otus.finalProject.dto.stats.TopScorersStatResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.persistence.model.*;
import org.otus.finalProject.persistence.repository.*;
import org.otus.finalProject.service.base.StatsService;
import org.otus.finalProject.service.kafka.KafkaStatsPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final GoalRepository goalRepository;
    private final KafkaStatsPublisher kafkaStatsPublisher;

    @Override
    @Transactional(readOnly = true)
    public TeamStatResponse teamStats(Long teamId, Integer year) {
        teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found: " + teamId));

        Instant[] period = getPeriod(year);
        Instant from = period[0];
        Instant to = period[1];

        List<Match> matches = matchRepository.findByMatchDateBetween(from, to).stream()
                .filter(m -> Objects.equals(m.getTeam1().getId(), teamId) || Objects.equals(m.getTeam2().getId(), teamId))
                .toList();

        int played = matches.size(), wins = 0, draws = 0, losses = 0;
        for (Match match : matches) {
            boolean home = Objects.equals(match.getTeam1().getId(), teamId);
            int forTeam = home ? match.getTeam1Score() : match.getTeam2Score();
            int against = home ? match.getTeam2Score() : match.getTeam1Score();
            if (forTeam > against) {
                wins++;
            } else if (forTeam == against) {
                draws++;
            } else {
                losses++;
            }
        }

        var response = new TeamStatResponse(teamId, year, played, wins, draws, losses);
        kafkaStatsPublisher.sendTeamStats(response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerStatResponse playerStats(Long playerId, Integer year) {
        playerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Player not found: " + playerId));

        Instant[] period = getPeriod(year);
        Instant from = period[0];
        Instant to = period[1];

        int matchesPlayed = matchPlayerRepository.countByPlayer_IdAndMatch_MatchDateBetween(playerId, from, to);
        int goals = goalRepository.countByPlayer_IdAndMatch_MatchDateBetween(playerId, from, to);
        double averageGoals = matchesPlayed == 0 ? 0.0 : (double) goals / matchesPlayed;

        var response = new PlayerStatResponse(playerId, year, matchesPlayed, goals, averageGoals);
        kafkaStatsPublisher.sendPlayerStats(response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamStatResponse> topTeams(Integer year, Integer limit) {
        Instant[] period = getPeriod(year);
        Instant from = period[0];
        Instant to = period[1];

        List<Match> matches = matchRepository.findByMatchDateBetween(from, to);
        Map<Long, int[]> table = new HashMap<>(); // array int[4] = {played, wins, draws, losses}

        for (Match m : matches) {
            long team1Id = m.getTeam1().getId();
            long teamId2 = m.getTeam2().getId();
            int scoreTeam1 = m.getTeam1Score();
            int scoreTeam2 = m.getTeam2Score();

            table.putIfAbsent(team1Id, new int[4]);
            table.putIfAbsent(teamId2, new int[4]);

            int[] statsTeam1 = table.get(team1Id);
            int[] statsTeam2 = table.get(teamId2);

            statsTeam1[0]++;
            statsTeam2[0]++;

            if (scoreTeam1 > scoreTeam2) {
                statsTeam1[1]++;
                statsTeam2[3]++;
            }       // win / loss
            else if (scoreTeam1 < scoreTeam2) {
                statsTeam2[1]++;
                statsTeam1[3]++;
            }  // win / loss
            else {
                statsTeam1[2]++;
                statsTeam2[2]++;
            }               // draw
        }

        var response = table.entrySet().stream()
                .map(e -> {
                    int[] stat = e.getValue();
                    return new TeamStatResponse(e.getKey(), year, stat[0], stat[1], stat[2], stat[3]);
                })
                .sorted(Comparator
                        .comparingInt(TeamStatResponse::wins).reversed()
                        .thenComparing(TeamStatResponse::draws, Comparator.reverseOrder())
                        .thenComparing(TeamStatResponse::losses)
                        .thenComparing(TeamStatResponse::played, Comparator.reverseOrder())
                        .thenComparing(TeamStatResponse::teamId))
                .limit(Math.max(1, limit == null ? 10 : limit))
                .toList();
        kafkaStatsPublisher.sendTopTeams(response, year, limit);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopScorersStatResponse> topScorers(Long teamId, Integer year, Integer limit) {
        Instant[] period = getPeriod(year);
        Instant from = period[0];
        Instant to = period[1];

        List<Goal> goals = goalRepository.findByMatch_MatchDateBetween(from, to);

        Map<Long, Map<Long, Integer>> goalsByPlayerByTeam = new HashMap<>();
        for (Goal g : goals) {
            Long playerId = g.getPlayer().getId();
            Long matchId = g.getMatch().getId();

            MatchPlayerId mpId = new MatchPlayerId(matchId, playerId);
            MatchPlayer mp = matchPlayerRepository.findById(mpId)
                    .orElse(null);

            if (mp == null) continue;
            Long teamAtThatMatch = mp.getTeam().getId();

            if (teamId != null && !Objects.equals(teamAtThatMatch, teamId)) continue;

            goalsByPlayerByTeam
                    .computeIfAbsent(playerId, k -> new HashMap<>())
                    .merge(teamAtThatMatch, 1, Integer::sum);
        }

        List<TopScorersStatResponse> rows = new ArrayList<>();
        for (var e : goalsByPlayerByTeam.entrySet()) {
            Long playerId = e.getKey();
            Player p = playerRepository.findById(playerId).orElse(null);
            if (p == null) continue;

            for (var e2 : e.getValue().entrySet()) {
                Long teamAtThatMatch = e2.getKey();
                int count = e2.getValue();
                rows.add(new TopScorersStatResponse(
                        playerId, p.getFirstName(), p.getLastName(), teamAtThatMatch, count
                ));
            }
        }

        var response = rows.stream()
                .sorted(Comparator
                        .comparingInt(TopScorersStatResponse::goals).reversed()
                        .thenComparing(TopScorersStatResponse::lastName)
                        .thenComparing(TopScorersStatResponse::firstName))
                .limit(Math.max(1, limit == null ? 10 : limit))
                .toList();
        kafkaStatsPublisher.sendTopScorers(response, teamId, year, limit);
        return response;
    }

    private Instant[] getPeriod(Integer year) {
        Instant from, to;
        if (year == null) {
            from = Instant.EPOCH;
            to = Instant.ofEpochMilli(Long.MAX_VALUE);
        } else {
            ZoneOffset z = ZoneOffset.UTC;
            from = LocalDate.of(year, 1, 1).atStartOfDay().toInstant(z);
            to = LocalDate.of(year + 1, 1, 1).atStartOfDay().toInstant(z);
        }
        return new Instant[]{from, to};
    }
}
