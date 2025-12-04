package org.otus.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.coach.CoachCreateRequest;
import org.otus.finalProject.dto.coach.CoachPatchRequest;
import org.otus.finalProject.dto.coach.CoachResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.mapper.CoachMapper;
import org.otus.finalProject.persistence.model.Coach;
import org.otus.finalProject.persistence.repository.CoachRepository;
import org.otus.finalProject.service.base.CoachService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CoachServiceImpl implements CoachService {
    private final CoachRepository repository;
    private final CoachMapper mapper;

    @Override
    public CoachResponse create(CoachCreateRequest request) {
        Coach saved = repository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CoachResponse findById(Long id) {
        Coach coach = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coach not found: " + id));
        return mapper.toResponse(coach);
    }

    @Override
    public CoachResponse patch(Long id, CoachPatchRequest patch) {
        Coach coach = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coach not found: " + id));
        mapper.applyPatch(coach, patch);
        return mapper.toResponse(coach);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Coach not found: " + id);
        }
        repository.deleteById(id);
    }
}
