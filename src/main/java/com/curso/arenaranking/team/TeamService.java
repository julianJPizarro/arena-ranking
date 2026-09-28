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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final TeamMapper teamMapper;

    @Transactional
    public TeamResponse create(TeamRequest request) {
        teamRepository.findByNameIgnoreCase(request.name()).ifPresent(e -> {
            throw new DataConflictException("A team with name '" + request.name() + "' already exists");
        });
        Team team = teamMapper.toEntity(request);
        return teamMapper.toResponse(teamRepository.save(team));
    }

    public Page<TeamResponse> list(String region, Pageable pageable) {
        Page<Team> page = StringUtils.hasText(region)
                ? teamRepository.findByRegionIgnoreCase(region, pageable)
                : teamRepository.findAll(pageable);
        return page.map(teamMapper::toResponse);
    }

    public TeamResponse getById(Long id) {
        return teamMapper.toResponse(findOrFail(id));
    }

    @Transactional
    public TeamResponse update(Long id, TeamRequest request) {
        Team team = findOrFail(id);
        teamRepository.findByNameIgnoreCase(request.name())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(e -> {
                    throw new DataConflictException("A team with name '" + request.name() + "' already exists");
                });
        teamMapper.updateEntity(request, team);
        return teamMapper.toResponse(teamRepository.save(team));
    }

    @Transactional
    public void delete(Long id) {
        Team team = findOrFail(id);
        teamRepository.delete(team);
    }

    public StatsResponse getStats(Long id) {
        findOrFail(id);
        long total = matchRepository.countByTeamId(id);
        long wins = matchRepository.countByTeamIdAndResult(id, MatchResult.WIN);
        long losses = matchRepository.countByTeamIdAndResult(id, MatchResult.LOSS);
        long draws = matchRepository.countByTeamIdAndResult(id, MatchResult.DRAW);
        double winRate = total == 0 ? 0.0 : (wins * 100.0) / total;
        return new StatsResponse(id, total, wins, losses, draws, winRate);
    }

    public StreakResponse getStreak(Long id) {
        findOrFail(id);
        List<Match> matches = matchRepository.findByTeamIdOrderByPlayedAtDesc(id);
        int streak = 0;
        for (Match match : matches) {
            if (match.getResult() == MatchResult.WIN) {
                streak++;
            } else {
                break;
            }
        }
        return new StreakResponse(id, streak);
    }

    public Team findOrFail(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id " + id));
    }
}
