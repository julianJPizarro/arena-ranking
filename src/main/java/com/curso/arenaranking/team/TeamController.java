package com.curso.arenaranking.team;

import com.curso.arenaranking.team.dto.StatsResponse;
import com.curso.arenaranking.team.dto.StreakResponse;
import com.curso.arenaranking.team.dto.TeamRequest;
import com.curso.arenaranking.team.dto.TeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "Management of teams participating in tournaments")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "Create a team")
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
        TeamResponse created = teamService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "List teams, paginated and with optional region filter")
    public Page<TeamResponse> list(
            @RequestParam(required = false) String region,
            Pageable pageable) {
        return teamService.list(region, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a team by id")
    public TeamResponse getById(@PathVariable Long id) {
        return teamService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a team")
    public TeamResponse update(@PathVariable Long id, @Valid @RequestBody TeamRequest request) {
        return teamService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a team")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get team stats (total, wins, losses, draws, win rate)")
    public StatsResponse getStats(@PathVariable Long id) {
        return teamService.getStats(id);
    }

    @GetMapping("/{id}/streak")
    @Operation(summary = "Get the team's current winning streak")
    public StreakResponse getStreak(@PathVariable Long id) {
        return teamService.getStreak(id);
    }
}
