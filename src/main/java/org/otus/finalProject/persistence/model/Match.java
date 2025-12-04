package org.otus.finalProject.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"team1", "team2", "championship"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "match",
        indexes = {
                @Index(name = "idx_match_date", columnList = "match_date"),
                @Index(name = "ux_match_teams_date", columnList = "team1_id,team2_id,match_date", unique = true)
        })
public class Match extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team1_id", nullable = false)
    private Team team1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team2_id", nullable = false)
    private Team team2;

    @Column(name = "team1_score", nullable = false)
    private Integer team1Score;

    @Column(name = "team2_score", nullable = false)
    private Integer team2Score;

    @Column(name = "match_date", nullable = false)
    private Instant matchDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "championship_id")
    private Championship championship;
}
