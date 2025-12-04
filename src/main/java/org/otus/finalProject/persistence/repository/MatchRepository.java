package org.otus.finalProject.persistence.repository;

import jakarta.validation.constraints.NotNull;
import org.otus.finalProject.persistence.model.Match;
import org.otus.finalProject.persistence.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByTeam1AndTeam2AndMatchDate(Team team1, Team team2, Instant matchDate);

    boolean existsByTeam1AndTeam2AndMatchDate(Team t1, Team t2, @NotNull Instant instant);

    List<Match> findAllByOrderByMatchDateDesc();

    List<Match> findByMatchDateBetween(Instant from, Instant to);
}

