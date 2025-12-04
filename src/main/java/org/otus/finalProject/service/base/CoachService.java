package org.otus.finalProject.service.base;

import org.otus.finalProject.dto.coach.CoachCreateRequest;
import org.otus.finalProject.dto.coach.CoachPatchRequest;
import org.otus.finalProject.dto.coach.CoachResponse;

import java.util.List;


public interface CoachService {
    CoachResponse create(CoachCreateRequest request);

    List<CoachResponse> findAll();

    CoachResponse findById(Long id);

    CoachResponse patch(Long id, CoachPatchRequest patch);

    void delete(Long id);
}
