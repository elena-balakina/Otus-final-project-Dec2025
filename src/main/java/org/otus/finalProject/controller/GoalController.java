package org.otus.finalProject.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.goal.GoalCreateRequest;
import org.otus.finalProject.dto.goal.GoalPatchRequest;
import org.otus.finalProject.dto.goal.GoalResponse;
import org.otus.finalProject.service.base.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Goals", description = "CRUDs for goals")
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse create(@RequestBody @Valid GoalCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<GoalResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public GoalResponse findOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/player/{playerId}")
    public List<GoalResponse> findByPlayer(@PathVariable Long playerId) {
        return service.findByPlayer(playerId);
    }

    @PatchMapping("/{id}")
    public GoalResponse patch(@PathVariable Long id, @RequestBody @Valid GoalPatchRequest request) {
        return service.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
