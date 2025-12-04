package org.otus.finalProject.persistence.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class MatchPlayerId implements Serializable {
    private Long matchId;
    private Long playerId;

    public MatchPlayerId() {
    }

    public MatchPlayerId(Long matchId, Long playerId) {
        this.matchId = matchId;
        this.playerId = playerId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchPlayerId that)) return false;
        return Objects.equals(matchId, that.matchId) && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, playerId);
    }
}

