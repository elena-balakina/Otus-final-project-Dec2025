package org.otus.finalProject.persistence.repository;

import org.otus.finalProject.persistence.model.Championship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChampionshipRepository extends JpaRepository<Championship, Long> {
    Optional<Championship> findByName(String name);

    boolean existsByNameIgnoreCase(String name);
}

