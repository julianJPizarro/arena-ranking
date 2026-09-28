package com.curso.arenaranking.team.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TeamResponse(
        Long id,
        String name,
        String tag,
        String region,
        String logoUrl,
        LocalDate foundedDate,
        LocalDateTime createdAt
) {
}
