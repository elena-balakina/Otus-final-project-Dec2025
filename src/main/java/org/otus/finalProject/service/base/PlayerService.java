package org.otus.finalProject.service.base;

import org.otus.finalProject.dto.player.PlayerCreateRequest;
import org.otus.finalProject.dto.player.PlayerPatchRequest;
import org.otus.finalProject.dto.player.PlayerResponse;

import java.util.List;


public interface PlayerService {
    PlayerResponse create(PlayerCreateRequest request);

    List<PlayerResponse> findAll();

    PlayerResponse findById(Long id);

    PlayerResponse patch(Long id, PlayerPatchRequest patch);

    void delete(Long id);
}
