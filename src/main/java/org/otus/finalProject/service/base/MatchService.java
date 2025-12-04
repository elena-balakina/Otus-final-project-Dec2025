package org.otus.finalProject.service.base;

import org.apache.coyote.BadRequestException;
import org.otus.finalProject.dto.match.MatchCreateRequest;
import org.otus.finalProject.dto.match.MatchPatchRequest;
import org.otus.finalProject.dto.match.MatchResponse;
import org.otus.finalProject.dto.match.MatchResultRequest;

import java.util.List;


public interface MatchService {
    MatchResponse create(MatchCreateRequest request) throws BadRequestException;

    List<MatchResponse> findAll();

    MatchResponse findById(Long id);

    MatchResponse patch(Long id, MatchPatchRequest patch) throws BadRequestException;

    MatchResponse setResult(Long id, MatchResultRequest request);

    void delete(Long id);
}
