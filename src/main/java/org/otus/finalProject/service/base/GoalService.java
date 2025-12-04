package org.otus.finalProject.service.base;

import org.otus.finalProject.dto.goal.GoalCreateRequest;
import org.otus.finalProject.dto.goal.GoalPatchRequest;
import org.otus.finalProject.dto.goal.GoalResponse;

import java.util.List;


public interface GoalService {
    GoalResponse create(GoalCreateRequest request);

    List<GoalResponse> findAll();

    GoalResponse findById(Long id);

    List<GoalResponse> findByPlayer(Long playerId);

    GoalResponse patch(Long id, GoalPatchRequest patch);

    void delete(Long id);
}
