package org.otus.finalProject.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.otus.finalProject.dto.match.MatchCreateRequest;
import org.otus.finalProject.dto.match.MatchPatchRequest;
import org.otus.finalProject.dto.match.MatchResponse;
import org.otus.finalProject.dto.match.MatchResultRequest;
import org.otus.finalProject.service.base.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Matches", description = "CRUDs for matches")
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {
    private final MatchService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatchResponse create(@RequestBody @Valid MatchCreateRequest request) throws BadRequestException {
        return service.create(request);
    }

    @GetMapping
    public List<MatchResponse> findAll() {
        return service.findAll();
    }


    @GetMapping("/{id}")
    public MatchResponse findOne(@PathVariable Long id) {
        return service.findById(id);
    }


    @PatchMapping("/{id}")
    public MatchResponse patch(@PathVariable Long id, @RequestBody @Valid MatchPatchRequest request) throws BadRequestException {
        return service.patch(id, request);
    }


    @PostMapping("/{id}/result")
    public MatchResponse setResult(@PathVariable Long id, @RequestBody @Valid MatchResultRequest request) {
        return service.setResult(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
