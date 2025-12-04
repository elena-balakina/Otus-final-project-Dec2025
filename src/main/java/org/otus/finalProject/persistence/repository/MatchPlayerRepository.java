package org.otus.finalProject.persistence.repository;

import org.otus.finalProject.persistence.model.Match;
import org.otus.finalProject.persistence.model.MatchPlayer;
import org.otus.finalProject.persistence.model.MatchPlayerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, MatchPlayerId> {
    List<MatchPlayer> findByMatch(Match match);

    void deleteByMatchId(Long id);

    int countByPlayer_IdAndMatch_MatchDateBetween(Long playerId, Instant from, Instant to);

    @Modifying
    @Query("delete from MatchPlayer mp where mp.match.id = :matchId and mp.team.id = :teamId")
    void deleteByMatchIdAndTeamId(@Param("matchId") Long matchId, @Param("teamId") Long teamId);

    boolean existsById(MatchPlayerId id);

    List<MatchPlayer> findByMatch_Id(Long id);
}

