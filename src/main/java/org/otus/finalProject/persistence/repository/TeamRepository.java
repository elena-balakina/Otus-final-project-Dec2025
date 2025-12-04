package org.otus.finalProject.persistence.repository;

import org.otus.finalProject.persistence.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);

    @Query("""
        select t from Team t
        left join fetch t.players p
        where t.id = :id
    """)
    Optional<Team> findByIdWithPlayers(@Param("id") Long id);
}

