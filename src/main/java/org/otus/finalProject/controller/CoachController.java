package org.otus.finalProject.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.coach.CoachCreateRequest;
import org.otus.finalProject.dto.coach.CoachPatchRequest;
import org.otus.finalProject.dto.coach.CoachResponse;
import org.otus.finalProject.service.base.CoachService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Coaches", description = "CRUDs for coaches")
@RestController
@RequestMapping("/api/coaches")
@RequiredArgsConstructor
public class CoachController {
    private final CoachService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoachResponse create(@RequestBody @Valid CoachCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<CoachResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CoachResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}")
    public CoachResponse patch(@PathVariable Long id, @RequestBody @Valid CoachPatchRequest patch) {
        return service.patch(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
