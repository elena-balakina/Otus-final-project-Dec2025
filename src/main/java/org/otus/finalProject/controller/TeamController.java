package org.otus.finalProject.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.team.TeamCreateRequest;
import org.otus.finalProject.dto.team.TeamPatchRequest;
import org.otus.finalProject.dto.team.TeamResponse;
import org.otus.finalProject.service.base.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Teams", description = "CRUDs for teams")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse create(@RequestBody @Valid TeamCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<TeamResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TeamResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}")
    public TeamResponse patch(@PathVariable Long id, @RequestBody @Valid TeamPatchRequest patch) {
        return service.patch(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
