package org.otus.finalProject.service.base;

import org.otus.finalProject.dto.championship.ChampionshipCreateRequest;
import org.otus.finalProject.dto.championship.ChampionshipPatchRequest;
import org.otus.finalProject.dto.championship.ChampionshipResponse;

import java.util.List;


public interface ChampionshipService {
    ChampionshipResponse create(ChampionshipCreateRequest request);

    List<ChampionshipResponse> findAll();

    ChampionshipResponse findById(Long id);

    ChampionshipResponse patch(Long id, ChampionshipPatchRequest patch);

    void delete(Long id);
}
