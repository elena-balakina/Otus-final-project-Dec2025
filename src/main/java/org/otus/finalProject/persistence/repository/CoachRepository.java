package org.otus.finalProject.persistence.repository;

import org.otus.finalProject.persistence.model.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, Long> {
}
