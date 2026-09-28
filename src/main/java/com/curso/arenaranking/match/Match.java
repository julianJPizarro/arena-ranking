package com.curso.arenaranking.match;

import com.curso.arenaranking.team.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false, length = 100)
    private String opponent;

    @Column(length = 100)
    private String tournament;

    @Column(name = "team_score", nullable = false)
    private Integer teamScore;

    @Column(name = "opponent_score", nullable = false)
    private Integer opponentScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MatchResult result;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.result = MatchResult.calculate(teamScore, opponentScore);
    }

    @PreUpdate
    void onUpdate() {
        this.result = MatchResult.calculate(teamScore, opponentScore);
    }
}
