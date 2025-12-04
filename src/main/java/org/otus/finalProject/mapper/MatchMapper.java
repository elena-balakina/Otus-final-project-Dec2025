package org.otus.finalProject.mapper;

import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.match.LineupItemResponse;
import org.otus.finalProject.dto.match.MatchResponse;
import org.otus.finalProject.persistence.model.Match;
import org.otus.finalProject.persistence.model.MatchPlayer;
import org.otus.finalProject.persistence.repository.MatchPlayerRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchMapper {

    private final MatchPlayerRepository matchPlayerRepository;

    public MatchResponse toResponse(Match match) {
        Long championshipId = match.getChampionship() != null ? match.getChampionship().getId() : null;
        List<MatchPlayer> participants = matchPlayerRepository.findByMatch_Id(match.getId());
        var team1Id = match.getTeam1().getId();
        var team2Id = match.getTeam2().getId();

        List<LineupItemResponse> lineupTeam1 = participants.stream()
                .filter(mp -> mp.getTeam().getId().equals(team1Id))
                .map(this::toLineupItem)
                .toList();

        List<LineupItemResponse> lineupTeam2 = participants.stream()
                .filter(mp -> mp.getTeam().getId().equals(team2Id))
                .map(this::toLineupItem)
                .toList();

        return new MatchResponse(
                match.getId(),
                match.getTeam1().getId(),
                match.getTeam2().getId(),
                match.getTeam1Score(),
                match.getTeam2Score(),
                match.getMatchDate(),
                championshipId,
                lineupTeam1,
                lineupTeam2
        );
    }

    private LineupItemResponse toLineupItem(MatchPlayer mp) {
        return new LineupItemResponse(
                mp.getPlayer().getId(),
                mp.getPlayer().getFirstName(),
                mp.getPlayer().getLastName(),
                mp.isStarting(),
                mp.getMinutesPlayed()
        );
    }
}
