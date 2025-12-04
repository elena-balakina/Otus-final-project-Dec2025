package org.otus.finalProject.mapper;

import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.player.PlayerShortResponse;
import org.otus.finalProject.dto.team.TeamCreateRequest;
import org.otus.finalProject.dto.team.TeamPatchRequest;
import org.otus.finalProject.dto.team.TeamResponse;
import org.otus.finalProject.persistence.model.Coach;
import org.otus.finalProject.persistence.model.Player;
import org.otus.finalProject.persistence.model.Team;
import org.otus.finalProject.persistence.repository.CoachRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeamMapper {
    private final CoachRepository coachRepository;

    public Team toEntity(TeamCreateRequest request) {
        Team team = new Team();
        team.setName(request.name());
        team.setCountry(request.country());
        if (request.coachId() != null) {
            coachRepository.findById(request.coachId()).ifPresent(team::setCoach);
        }
        return team;
    }

    public TeamResponse toResponse(Team team) {
        Long coachId = team.getCoach() != null ? team.getCoach().getId() : null;
        Set<PlayerShortResponse> players = team.getPlayers() != null
                ? team.getPlayers().stream()
                .map(this::toShortPlayerResponse)
                .collect(Collectors.toSet())
                : null;

        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getCountry(),
                coachId,
                players
        );
    }

    public void applyPatch(Team team, TeamPatchRequest patch) {
        if (patch.name() != null) team.setName(patch.name());
        if (patch.country() != null) team.setCountry(patch.country());
        if (patch.coachId() != null) {
            Coach coach = coachRepository.findById(patch.coachId()).orElse(null);
            team.setCoach(coach);
        }
    }

    private PlayerShortResponse toShortPlayerResponse(Player player) {
        return new PlayerShortResponse(player.getId(), player.getFirstName(), player.getLastName());
    }
}
