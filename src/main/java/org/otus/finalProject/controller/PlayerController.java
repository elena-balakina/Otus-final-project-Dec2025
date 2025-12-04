package org.otus.finalProject.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.otus.finalProject.dto.player.PlayerCreateRequest;
import org.otus.finalProject.dto.player.PlayerPatchRequest;
import org.otus.finalProject.dto.player.PlayerResponse;
import org.otus.finalProject.service.base.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Players", description = "CRUDs for players")
@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerResponse create(@RequestBody @Valid PlayerCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<PlayerResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PlayerResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}")
    public PlayerResponse patch(@PathVariable Long id,
                                @RequestBody @Valid PlayerPatchRequest request) {
        return service.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
