package org.otus.finalProject.persistence.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"match", "player", "team"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "match_player",
        indexes = {
                @Index(name = "pk_match_player", columnList = "match_id,player_id", unique = true)
        })
public class MatchPlayer extends BaseEntity {
    @EmbeddedId
    private MatchPlayerId id = new MatchPlayerId();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("matchId")
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("playerId")
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "is_starting", nullable = false)
    private boolean starting = true;

    @Column(name = "minutes_played")
    private Integer minutesPlayed;
}

