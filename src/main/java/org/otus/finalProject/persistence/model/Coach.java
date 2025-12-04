package org.otus.finalProject.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"teams"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "coach",
        indexes = {
                @Index(name = "idx_coach_last_name", columnList = "last_name")
        })
public class Coach extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 60, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 60, nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "coach")
    private Set<Team> teams = new HashSet<>();
}

