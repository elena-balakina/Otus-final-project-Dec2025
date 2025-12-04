package org.otus.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.team.TeamCreateRequest;
import org.otus.finalProject.dto.team.TeamPatchRequest;
import org.otus.finalProject.dto.team.TeamResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.mapper.TeamMapper;
import org.otus.finalProject.persistence.model.Player;
import org.otus.finalProject.persistence.model.Team;
import org.otus.finalProject.persistence.repository.CoachRepository;
import org.otus.finalProject.persistence.repository.PlayerRepository;
import org.otus.finalProject.persistence.repository.TeamRepository;
import org.otus.finalProject.service.base.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final CoachRepository coachRepository;
    private final PlayerRepository playerRepository;
    private final TeamMapper mapper;

    @Override
    public TeamResponse create(TeamCreateRequest request) {
        Team team = mapper.toEntity(request);
        Team saved = teamRepository.save(team);
        if (request.playerIds() != null && !request.playerIds().isEmpty()) {
            attachPlayersToTeam(saved, request.playerIds());
        }
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> findAll() {
        return teamRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse findById(Long id) {
        Team team = teamRepository.findByIdWithPlayers(id)
                .orElseThrow(() -> new NotFoundException("Team not found: " + id));
        return mapper.toResponse(team);
    }

    @Override
    public TeamResponse patch(Long id, TeamPatchRequest patch) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Team not found: " + id));
        mapper.applyPatch(team, patch);
        if (patch.playerIds() != null) {
            attachPlayersToTeam(team, patch.playerIds());
        }
        teamRepository.flush();
        Team updated = teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Team not found after update: " + id));
        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Team not found: " + id));
        team.setCoach(null);
        List<Player> current = playerRepository.findAllByTeam_Id(team.getId());
        for (Player player : current) {
            player.setTeam(null);
        }
        teamRepository.delete(team);
    }

    private void attachPlayersToTeam(Team team, Set<Long> playerIds) {
        List<Player> currentPlayers = playerRepository.findAllByTeam_Id(team.getId());
        List<Player> toAssignPlayersList = playerIds.isEmpty()
                ? Collections.emptyList()
                : playerRepository.findAllByIdIn(playerIds);

        if (toAssignPlayersList.size() != playerIds.size()) {
            Set<Long> found = toAssignPlayersList.stream().map(Player::getId).collect(Collectors.toSet());
            Set<Long> missing = new HashSet<>(playerIds);
            missing.removeAll(found);
            throw new NotFoundException("Players not found: " + missing);
        }

        Set<Long> toAssignIds = toAssignPlayersList.stream().map(Player::getId).collect(Collectors.toSet());

        // remove teamId for players that are not in the team anymore
        for (Player p : currentPlayers) {
            if (!toAssignIds.contains(p.getId())) {
                p.setTeam(null);
            }
        }

        // set teamId for new players
        for (Player p : toAssignPlayersList) {
            p.setTeam(team);
        }

        // synchronize
        if (team.getPlayers() != null) {
            team.getPlayers().clear();
            team.getPlayers().addAll(toAssignPlayersList);
        }
    }
}
