package org.otus.finalProject.persistence.repository;

import org.otus.finalProject.persistence.model.Player;
import org.otus.finalProject.persistence.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByTeamOrderByLastNameAsc(Team team);
    List<Player> findAllByIdIn(Set<Long> ids);
    List<Player> findAllByTeam_Id(Long teamId);
}

