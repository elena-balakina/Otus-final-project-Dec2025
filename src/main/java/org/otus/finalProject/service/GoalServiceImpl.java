package org.otus.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.goal.GoalCreateRequest;
import org.otus.finalProject.dto.goal.GoalPatchRequest;
import org.otus.finalProject.dto.goal.GoalResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.mapper.GoalMapper;
import org.otus.finalProject.persistence.model.*;
import org.otus.finalProject.persistence.repository.GoalRepository;
import org.otus.finalProject.persistence.repository.MatchPlayerRepository;
import org.otus.finalProject.persistence.repository.MatchRepository;
import org.otus.finalProject.persistence.repository.PlayerRepository;
import org.otus.finalProject.service.base.GoalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final GoalMapper mapper;

    @Override
    @Transactional
    public GoalResponse create(GoalCreateRequest request) {
        Match match = matchRepository.findById(request.matchId())
                .orElseThrow(() -> new NotFoundException("Match not found: " + request.matchId()));
        Player player = playerRepository.findById(request.playerId())
                .orElseThrow(() -> new NotFoundException("Player not found: " + request.playerId()));

        validateGoalTime(request.goalTime());
        ensureParticipation(match, player);

        Goal goal = new Goal();
        goal.setMatch(match);
        goal.setPlayer(player);
        goal.setGoalTime(request.goalTime());

        return mapper.toResponse(goalRepository.save(goal));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponse> findAll() {
        return goalRepository.findAllByOrderByIdAsc().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GoalResponse findById(Long id) {
        Goal g = goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Goal not found: " + id));
        return mapper.toResponse(g);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponse> findByPlayer(Long playerId) {
        return goalRepository.findByPlayer_IdOrderByIdAsc(playerId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public GoalResponse patch(Long id, GoalPatchRequest patch) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Goal not found: " + id));

        if (patch.goalTime() != null) {
            validateGoalTime(patch.goalTime());
            goal.setGoalTime(patch.goalTime());
        }

        Match newMatch = goal.getMatch();
        Player newPlayer = goal.getPlayer();

        if (patch.matchId() != null) {
            newMatch = matchRepository.findById(patch.matchId())
                    .orElseThrow(() -> new NotFoundException("Match not found: " + patch.matchId()));
            goal.setMatch(newMatch);
        }
        if (patch.playerId() != null) {
            newPlayer = playerRepository.findById(patch.playerId())
                    .orElseThrow(() -> new NotFoundException("Player not found: " + patch.playerId()));
            goal.setPlayer(newPlayer);
        }

        ensureParticipation(newMatch, newPlayer);
        return mapper.toResponse(goal);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!goalRepository.existsById(id)) {
            throw new NotFoundException("Goal not found: " + id);
        }
        goalRepository.deleteById(id);
    }

    private void validateGoalTime(Integer goalTime) {
        if (goalTime == null || goalTime < 0 || goalTime > 120) {
            throw new IllegalArgumentException("goalTime must be in [0..120]");
        }
    }

    /**
     * Check that the player is in the match_player table
     * If not — create MatchPlayer with isStarting=false, minutesPlayed=null.
     * Also, check player is in the team.
     */
    private void ensureParticipation(Match match, Player player) {
        if (player.getTeam() == null) {
            throw new IllegalArgumentException("Player has no team: " + player.getId());
        }
        Long playerTeamId = player.getTeam().getId();
        Long team1Id = match.getTeam1().getId();
        Long team2Id = match.getTeam2().getId();

        if (!Objects.equals(playerTeamId, team1Id) && !Objects.equals(playerTeamId, team2Id)) {
            throw new IllegalArgumentException(
                    "Player " + player.getId() + " does not belong to either team in this match");
        }

        MatchPlayerId id = new MatchPlayerId(match.getId(), player.getId());
        if (matchPlayerRepository.existsById(id)) {
            return;
        }

        MatchPlayer mp = new MatchPlayer();
        mp.setId(id);
        mp.setMatch(match);
        mp.setPlayer(player);
        mp.setTeam(Objects.equals(playerTeamId, team1Id) ? match.getTeam1() : match.getTeam2());
        mp.setStarting(false);
        mp.setMinutesPlayed(null);

        matchPlayerRepository.save(mp);
    }
}
