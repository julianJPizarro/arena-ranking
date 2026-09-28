package com.curso.arenaranking.match;

import com.curso.arenaranking.exceptions.ResourceNotFoundException;
import com.curso.arenaranking.match.dto.MatchRequest;
import com.curso.arenaranking.match.dto.MatchResponse;
import com.curso.arenaranking.team.Team;
import com.curso.arenaranking.team.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private MatchMapper matchMapper;

    @InjectMocks
    private MatchService matchService;

    private Team team;
    private MatchRequest request;
    private Match match;

    @BeforeEach
    void setUp() {
        team = Team.builder().id(1L).name("Team Vortex").tag("TVX").build();
        request = new MatchRequest("Rival A", "Cup2026", 3, 1, LocalDateTime.now());
        match = Match.builder().id(10L).team(team).opponent("Rival A")
                .teamScore(3).opponentScore(1).result(MatchResult.WIN).build();
    }

    @Test
    void shouldCreateMatchLinkedToExistingTeam() {
        when(teamService.findOrFail(1L)).thenReturn(team);
        when(matchMapper.toEntity(request)).thenReturn(match);
        when(matchRepository.save(match)).thenReturn(match);
        when(matchMapper.toResponse(match)).thenReturn(
                new MatchResponse(10L, 1L, "Rival A", "Cup2026", 3, 1, MatchResult.WIN, null, null));

        MatchResponse response = matchService.create(1L, request);

        assertThat(response.opponent()).isEqualTo("Rival A");
        assertThat(match.getTeam()).isEqualTo(team);
        verify(matchRepository).save(match);
    }

    @Test
    void shouldThrowWhenTeamDoesNotExistOnCreate() {
        when(teamService.findOrFail(99L)).thenThrow(new ResourceNotFoundException("Team not found with id 99"));

        assertThatThrownBy(() -> matchService.create(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(matchRepository);
    }

    @Test
    void shouldThrowWhenMatchDoesNotExistOnUpdate() {
        when(matchRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchService.update(1L, 10L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenMatchBelongsToDifferentTeam() {
        Team otherTeam = Team.builder().id(2L).name("Other Team").tag("OTH").build();
        Match matchOfOtherTeam = Match.builder().id(10L).team(otherTeam).build();
        when(matchRepository.findById(10L)).thenReturn(Optional.of(matchOfOtherTeam));

        assertThatThrownBy(() -> matchService.update(1L, 10L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDeleteMatchWhenItBelongsToTeam() {
        when(matchRepository.findById(10L)).thenReturn(Optional.of(match));

        matchService.delete(1L, 10L);

        verify(matchRepository).delete(match);
    }
}
