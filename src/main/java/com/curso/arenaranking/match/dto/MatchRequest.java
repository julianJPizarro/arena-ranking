package com.curso.arenaranking.match.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record MatchRequest(
        @NotBlank(message = "Opponent is required")
        @Size(max = 100)
        String opponent,

        @Size(max = 100)
        String tournament,

        @NotNull(message = "Team score is required")
        @Min(value = 0, message = "Score cannot be negative")
        Integer teamScore,

        @NotNull(message = "Opponent score is required")
        @Min(value = 0, message = "Score cannot be negative")
        Integer opponentScore,

        @NotNull(message = "Played date is required")
        LocalDateTime playedAt
) {
}
