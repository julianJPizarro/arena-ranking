package com.curso.arenaranking.match.dto;

import com.curso.arenaranking.match.MatchResult;

import java.time.LocalDateTime;

public record MatchResponse(
        Long id,
        Long teamId,
        String opponent,
        String tournament,
        Integer teamScore,
        Integer opponentScore,
        MatchResult result,
        LocalDateTime playedAt,
        LocalDateTime createdAt
) {
}
