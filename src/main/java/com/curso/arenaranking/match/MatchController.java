package com.curso.arenaranking.match;

import com.curso.arenaranking.match.dto.MatchRequest;
import com.curso.arenaranking.match.dto.MatchResponse;
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
@RequestMapping("/teams/{teamId}/matches")
@RequiredArgsConstructor
@Tag(name = "Matches", description = "Management of a team's match history")
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    @Operation(summary = "Register a match for a team")
    public ResponseEntity<MatchResponse> create(
            @PathVariable Long teamId,
            @Valid @RequestBody MatchRequest request) {
        MatchResponse created = matchService.create(teamId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "List a team's matches, paginated and with optional tournament filter")
    public Page<MatchResponse> list(
            @PathVariable Long teamId,
            @RequestParam(required = false) String tournament,
            Pageable pageable) {
        return matchService.listByTeam(teamId, tournament, pageable);
    }

    @PutMapping("/{matchId}")
    @Operation(summary = "Update a match")
    public MatchResponse update(
            @PathVariable Long teamId,
            @PathVariable Long matchId,
            @Valid @RequestBody MatchRequest request) {
        return matchService.update(teamId, matchId, request);
    }

    @DeleteMapping("/{matchId}")
    @Operation(summary = "Delete a match")
    public ResponseEntity<Void> delete(@PathVariable Long teamId, @PathVariable Long matchId) {
        matchService.delete(teamId, matchId);
        return ResponseEntity.noContent().build();
    }
}
