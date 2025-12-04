package org.otus.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.otus.finalProject.dto.match.*;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.mapper.MatchMapper;
import org.otus.finalProject.persistence.model.*;
import org.otus.finalProject.persistence.repository.*;
import org.otus.finalProject.service.base.MatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final ChampionshipRepository championshipRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final GoalRepository goalRepository;
    private final MatchMapper mapper;

    @Override
    public MatchResponse create(MatchCreateRequest request) throws BadRequestException {
        if (request.team1Id().equals(request.team2Id())) {
            throw new IllegalArgumentException("team1Id and team2Id must be different");
        }

        Team team1 = teamRepository.findById(request.team1Id())
                .orElseThrow(() -> new NotFoundException("Team not found: " + request.team1Id()));
        Team team2 = teamRepository.findById(request.team2Id())
                .orElseThrow(() -> new NotFoundException("Team not found: " + request.team2Id()));

        Championship championship = null;
        if (request.championshipId() != null) {
            championship = championshipRepository.findById(request.championshipId())
                    .orElseThrow(() -> new NotFoundException("Championship not found: " + request.championshipId()));
        }

        if (matchRepository.existsByTeam1AndTeam2AndMatchDate(team1, team2, request.matchDate())) {
            throw new IllegalArgumentException("Match with the same teams and date already exists");
        }

        Match match = new Match();
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setTeam1Score(0);
        match.setTeam2Score(0);
        match.setMatchDate(request.matchDate());
        match.setChampionship(championship);
        match = matchRepository.save(match);

        saveLineup(match, team1, request.lineupTeam1());
        saveLineup(match, team2, request.lineupTeam2());

        return mapper.toResponse(match);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> findAll() {
        return matchRepository.findAllByOrderByMatchDateDesc().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MatchResponse findById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Match not found: " + id));
        return mapper.toResponse(match);
    }

    @Override
    public MatchResponse patch(Long id, MatchPatchRequest patch) throws BadRequestException {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Match not found: " + id));

        if (patch.team1Id() != null) {
            Team t1 = teamRepository.findById(patch.team1Id())
                    .orElseThrow(() -> new NotFoundException("Team not found: " + patch.team1Id()));
            if (!Objects.equals(t1.getId(), match.getTeam1().getId())) {
                matchPlayerRepository.deleteByMatchIdAndTeamId(match.getId(), match.getTeam1().getId());
            }
            match.setTeam1(t1);
        }
        if (patch.team2Id() != null) {
            Team t2 = teamRepository.findById(patch.team2Id())
                    .orElseThrow(() -> new NotFoundException("Team not found: " + patch.team2Id()));
            if (!Objects.equals(t2.getId(), match.getTeam2().getId())) {
                matchPlayerRepository.deleteByMatchIdAndTeamId(match.getId(), match.getTeam2().getId());
            }
            match.setTeam2(t2);
        }
        if (patch.team1Id() != null && patch.team2Id() != null && patch.team1Id().equals(patch.team2Id())) {
            throw new IllegalArgumentException("team1Id and team2Id must be different");
        }
        if (patch.matchDate() != null) {
            match.setMatchDate(patch.matchDate());
        }
        if (patch.championshipId() != null) {
            Championship championship = championshipRepository.findById(patch.championshipId())
                    .orElseThrow(() -> new NotFoundException("Championship not found: " + patch.championshipId()));
            match.setChampionship(championship);
        }

        if (patch.lineupTeam1() != null)
            saveLineup(match, match.getTeam1(), patch.lineupTeam1());

        if (patch.lineupTeam2() != null)
            saveLineup(match, match.getTeam2(), patch.lineupTeam2());

        return mapper.toResponse(match);
    }

    public MatchResponse setResult(Long id, MatchResultRequest request) {
        Match m = matchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Match not found: " + id));
        m.setTeam1Score(request.team1Score());
        m.setTeam2Score(request.team2Score());
        return mapper.toResponse(m);
    }

    @Override
    public void delete(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new NotFoundException("Match not found: " + id);
        }
        matchPlayerRepository.deleteByMatchId(id);
        goalRepository.deleteByMatchId(id);
        matchRepository.deleteById(id);
    }

    private void saveLineup(Match match, Team team, List<LineupItem> lineup) throws BadRequestException {
        if (lineup == null) return;

        // validate data
        Set<Long> validated = new HashSet<>();
        Map<Long, Player> players = new HashMap<>(lineup.size());

        for (LineupItem item : lineup) {
            Long playerId = item.playerId();
            if (playerId == null) throw new BadRequestException("playerId is required in lineup");
            if (!validated.add(playerId)) throw new BadRequestException("duplicate playerId: " + playerId);
            if (item.minutesPlayed() != null && (item.minutesPlayed() < 0 || item.minutesPlayed() > 120))
                throw new BadRequestException("minutesPlayed must be in [0..120]");

            Player p = playerRepository.findById(playerId)
                    .orElseThrow(() -> new NotFoundException("player not found: " + playerId));

            if (p.getTeam() == null || !Objects.equals(p.getTeam().getId(), team.getId()))
                throw new BadRequestException("player %d does not belong to team %d".formatted(p.getId(), team.getId()));

            players.put(playerId, p);
        }

        // clean previous lineup
        matchPlayerRepository.deleteByMatchIdAndTeamId(match.getId(), team.getId());

        // add new lineup
        for (LineupItem item : lineup) {
            Player p = players.get(item.playerId());

            MatchPlayer matchPlayer = new MatchPlayer();
            matchPlayer.setId(new MatchPlayerId(match.getId(), p.getId()));
            matchPlayer.setMatch(match);
            matchPlayer.setPlayer(p);
            matchPlayer.setTeam(team);
            matchPlayer.setStarting(item.isStarting() == null ? true : item.isStarting());
            matchPlayer.setMinutesPlayed(item.minutesPlayed());
            matchPlayerRepository.save(matchPlayer);
        }
    }
}
