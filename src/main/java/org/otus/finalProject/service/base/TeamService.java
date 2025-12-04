package org.otus.finalProject.service.base;

import org.otus.finalProject.dto.team.TeamCreateRequest;
import org.otus.finalProject.dto.team.TeamPatchRequest;
import org.otus.finalProject.dto.team.TeamResponse;

import java.util.List;


public interface TeamService {
    TeamResponse create(TeamCreateRequest request);

    List<TeamResponse> findAll();

    TeamResponse findById(Long id);

    TeamResponse patch(Long id, TeamPatchRequest patch);

    void delete(Long id);
}
