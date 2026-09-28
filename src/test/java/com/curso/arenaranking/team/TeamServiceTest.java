package com.curso.arenaranking.team;

import com.curso.arenaranking.exceptions.DataConflictException;
import com.curso.arenaranking.exceptions.ResourceNotFoundException;
import com.curso.arenaranking.match.Match;
import com.curso.arenaranking.match.MatchRepository;
import com.curso.arenaranking.match.MatchResult;
import com.curso.arenaranking.team.dto.StatsResponse;
import com.curso.arenaranking.team.dto.StreakResponse;
import com.curso.arenaranking.team.dto.TeamRequest;
import com.curso.arenaranking.team.dto.TeamResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamService teamService;

    private TeamRequest request;
    private Team team;

    @BeforeEach
    void setUp() {
        request = new TeamRequest("Team Vortex", "TVX", "LATAM", null, null);
        team = Team.builder().id(1L).name("Team Vortex").tag("TVX").region("LATAM").build();
    }

    @Test
    void shouldCreateTeamWhenNameIsNotTaken() {
        when(teamRepository.findByNameIgnoreCase("Team Vortex")).thenReturn(Optional.empty());
        when(teamMapper.toEntity(request)).thenReturn(team);
        when(teamRepository.save(team)).thenReturn(team);
        when(teamMapper.toResponse(team)).thenReturn(
                new TeamResponse(1L, "Team Vortex", "TVX", "LATAM", null, null, null));

        TeamResponse response = teamService.create(request);

        assertThat(response.name()).isEqualTo("Team Vortex");
        verify(teamRepository).save(team);
    }

    @Test
    void shouldRejectCreationWhenNameAlreadyExists() {
        when(teamRepository.findByNameIgnoreCase("Team Vortex")).thenReturn(Optional.of(team));

        assertThatThrownBy(() -> teamService.create(request))
                .isInstanceOf(DataConflictException.class);

        verify(teamRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenTeamNotFoundById() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldComputeStatsFromMatchCounts() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(matchRepository.countByTeamId(1L)).thenReturn(4L);
        when(matchRepository.countByTeamIdAndResult(1L, MatchResult.WIN)).thenReturn(3L);
        when(matchRepository.countByTeamIdAndResult(1L, MatchResult.LOSS)).thenReturn(1L);
        when(matchRepository.countByTeamIdAndResult(1L, MatchResult.DRAW)).thenReturn(0L);

        StatsResponse stats = teamService.getStats(1L);

        assertThat(stats.totalMatches()).isEqualTo(4);
        assertThat(stats.wins()).isEqualTo(3);
        assertThat(stats.winRate()).isEqualTo(75.0);
    }

    @Test
    void shouldReturnZeroWinRateWhenNoMatchesPlayed() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(matchRepository.countByTeamId(1L)).thenReturn(0L);
        when(matchRepository.countByTeamIdAndResult(eq(1L), any())).thenReturn(0L);

        StatsResponse stats = teamService.getStats(1L);

        assertThat(stats.winRate()).isEqualTo(0.0);
    }

    @Test
    void shouldComputeCurrentStreakFromMostRecentConsecutiveWins() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        Match win1 = Match.builder().result(MatchResult.WIN).playedAt(LocalDateTime.now()).build();
        Match win2 = Match.builder().result(MatchResult.WIN).playedAt(LocalDateTime.now().minusDays(1)).build();
        Match loss = Match.builder().result(MatchResult.LOSS).playedAt(LocalDateTime.now().minusDays(2)).build();
        when(matchRepository.findByTeamIdOrderByPlayedAtDesc(1L)).thenReturn(List.of(win1, win2, loss));

        StreakResponse streak = teamService.getStreak(1L);

        assertThat(streak.currentStreak()).isEqualTo(2);
    }

    @Test
    void shouldReturnZeroStreakWhenMostRecentMatchWasNotAWin() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        Match loss = Match.builder().result(MatchResult.LOSS).playedAt(LocalDateTime.now()).build();
        when(matchRepository.findByTeamIdOrderByPlayedAtDesc(1L)).thenReturn(List.of(loss));

        StreakResponse streak = teamService.getStreak(1L);

        assertThat(streak.currentStreak()).isEqualTo(0);
    }
}
