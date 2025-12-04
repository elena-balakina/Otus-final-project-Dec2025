package org.otus.finalProject.mapper;

import org.otus.finalProject.dto.player.PlayerCreateRequest;
import org.otus.finalProject.dto.player.PlayerPatchRequest;
import org.otus.finalProject.dto.player.PlayerResponse;
import org.otus.finalProject.persistence.model.Player;
import org.otus.finalProject.persistence.model.Team;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public Player toEntity(PlayerCreateRequest request, Team team) {
        Player player = new Player();
        player.setFirstName(request.firstName());
        player.setLastName(request.lastName());
        player.setTeam(team);
        return player;
    }

    public PlayerResponse toResponse(Player player) {
        Team team = player.getTeam();
        return new PlayerResponse(
                player.getId(),
                player.getFirstName(),
                player.getLastName(),
                team != null ? team.getId() : null);
    }

    public void applyPatch(Player player, PlayerPatchRequest request, Team teamToAttach) {
        if (request.firstName() != null) {
            player.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            player.setLastName(request.lastName());
        }
        player.setTeam(teamToAttach);
    }

}
