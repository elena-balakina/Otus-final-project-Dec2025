package org.otus.finalProject.mapper;

import org.otus.finalProject.dto.coach.CoachCreateRequest;
import org.otus.finalProject.dto.coach.CoachPatchRequest;
import org.otus.finalProject.dto.coach.CoachResponse;
import org.otus.finalProject.persistence.model.Coach;
import org.otus.finalProject.persistence.model.Team;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CoachMapper {

    public Coach toEntity(CoachCreateRequest request) {
        Coach coach = new Coach();
        coach.setFirstName(request.firstName().trim());
        coach.setLastName(request.lastName().trim());
        return coach;
    }

    public void applyPatch(Coach entity, CoachPatchRequest request) {
        if (request.firstName() != null) {
            entity.setFirstName(request.firstName().trim());
        }
        if (request.lastName() != null) {
            entity.setLastName(request.lastName().trim());
        }
    }

    public CoachResponse toResponse(Coach coach) {
        Set<Long> teamIds = null;
        Set<Team> teams = coach.getTeams();
        if (teams != null) {
            teamIds = teams.stream()
                    .map(Team::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(HashSet::new));
        }
        return new CoachResponse(
                coach.getId(),
                coach.getFirstName(),
                coach.getLastName(),
                teamIds
        );
    }
}