package org.otus.finalProject.persistence.repository;

import org.otus.finalProject.persistence.model.Goal;
import org.otus.finalProject.persistence.model.Match;
import org.otus.finalProject.persistence.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByMatchAndPlayer(Match match, Player player);

    void deleteByMatchId(Long id);

    List<Goal> findAllByOrderByIdAsc();

    List<Goal> findByPlayer_IdOrderByIdAsc(Long playerId);

    int countByPlayer_IdAndMatch_MatchDateBetween(Long playerId, Instant from, Instant to);

    List<Goal> findByMatch_MatchDateBetween(Instant from, Instant to);
}
