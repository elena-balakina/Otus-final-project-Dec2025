package org.otus.finalProject.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.championship.ChampionshipCreateRequest;
import org.otus.finalProject.dto.championship.ChampionshipPatchRequest;
import org.otus.finalProject.dto.championship.ChampionshipResponse;
import org.otus.finalProject.service.base.ChampionshipService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Championships", description = "CRUDs for championships")
@RestController
@RequestMapping("/api/championships")
@RequiredArgsConstructor
public class ChampionshipController {
    private final ChampionshipService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChampionshipResponse create(@RequestBody @Valid ChampionshipCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ChampionshipResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ChampionshipResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}")
    public ChampionshipResponse patch(@PathVariable Long id, @RequestBody @Valid ChampionshipPatchRequest patch) {
        return service.patch(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
