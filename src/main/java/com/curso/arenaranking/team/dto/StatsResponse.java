package com.curso.arenaranking.team.dto;

public record StatsResponse(
        Long teamId,
        long totalMatches,
        long wins,
        long losses,
        long draws,
        double winRate
) {
}
