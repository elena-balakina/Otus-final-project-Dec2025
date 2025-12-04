package org.otus.finalProject.mapper;

import org.otus.finalProject.dto.goal.GoalResponse;
import org.otus.finalProject.persistence.model.Goal;
import org.springframework.stereotype.Component;

@Component
public class GoalMapper {
    public GoalResponse toResponse(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getMatch().getId(),
                goal.getPlayer().getId(),
                goal.getGoalTime()
        );
    }
}