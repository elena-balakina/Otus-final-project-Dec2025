package org.otus.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.player.PlayerCreateRequest;
import org.otus.finalProject.dto.player.PlayerPatchRequest;
import org.otus.finalProject.dto.player.PlayerResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.mapper.PlayerMapper;
import org.otus.finalProject.persistence.model.Player;
import org.otus.finalProject.persistence.model.Team;
import org.otus.finalProject.persistence.repository.PlayerRepository;
import org.otus.finalProject.persistence.repository.TeamRepository;
import org.otus.finalProject.service.base.PlayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final PlayerMapper mapper;

    @Override
    @Transactional
    public PlayerResponse create(PlayerCreateRequest request) {
        Team team = null;
        if (request.teamId() != null) {
            team = teamRepository.findById(request.teamId())
                    .orElseThrow(() -> new NotFoundException("Team not found: " + request.teamId()));
        }
        Player saved = playerRepository.save(mapper.toEntity(request, team));
        return mapper.toResponse(saved);
    }

    @Override
    public List<PlayerResponse> findAll() {
        return playerRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public PlayerResponse findById(Long id) {
        Player p = playerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Player not found: " + id));
        return mapper.toResponse(p);
    }

    @Override
    @Transactional
    public PlayerResponse patch(Long id, PlayerPatchRequest request) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Player not found: " + id));

        Team teamToAttach;
        if (request.teamId() != null) {
            teamToAttach = teamRepository.findById(request.teamId())
                    .orElseThrow(() -> new NotFoundException("Team not found: " + request.teamId()));
        } else {
            teamToAttach = null;
        }

        mapper.applyPatch(player, request, teamToAttach);
        return mapper.toResponse(player);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new NotFoundException("Player not found: " + id);
        }
        playerRepository.deleteById(id);
    }
}

