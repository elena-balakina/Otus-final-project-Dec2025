package org.otus.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.championship.ChampionshipCreateRequest;
import org.otus.finalProject.dto.championship.ChampionshipPatchRequest;
import org.otus.finalProject.dto.championship.ChampionshipResponse;
import org.otus.finalProject.handler.NotFoundException;
import org.otus.finalProject.mapper.ChampionshipMapper;
import org.otus.finalProject.persistence.model.Championship;
import org.otus.finalProject.persistence.repository.ChampionshipRepository;
import org.otus.finalProject.service.base.ChampionshipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChampionshipServiceImpl implements ChampionshipService {
    private final ChampionshipRepository repository;
    private final ChampionshipMapper mapper;

    @Override
    @Transactional
    public ChampionshipResponse create(ChampionshipCreateRequest request) {
        if (repository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Championship with this name " + request.name() + "already exists");
        }
        Championship saved = repository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChampionshipResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChampionshipResponse findById(Long id) {
        Championship championship = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Championship not found: " + id));
        return mapper.toResponse(championship);
    }

    @Override
    @Transactional
    public ChampionshipResponse patch(Long id, ChampionshipPatchRequest patch) {
        Championship championship = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Championship not found: " + id));
        mapper.applyPatch(championship, patch);
        return mapper.toResponse(repository.save(championship));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Championship not found: " + id);
        }
        repository.deleteById(id);
    }
}
