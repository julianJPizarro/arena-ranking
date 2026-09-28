package com.curso.arenaranking.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TeamRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Tag is required")
        @Size(max = 10)
        String tag,

        @Size(max = 50)
        String region,

        String logoUrl,

        LocalDate foundedDate
) {
}
