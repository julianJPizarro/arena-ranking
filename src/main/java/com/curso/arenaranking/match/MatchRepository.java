package com.curso.arenaranking.match;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Page<Match> findByTeamId(Long teamId, Pageable pageable);

    Page<Match> findByTeamIdAndTournamentIgnoreCase(Long teamId, String tournament, Pageable pageable);

    long countByTeamId(Long teamId);

    long countByTeamIdAndResult(Long teamId, MatchResult result);

    List<Match> findByTeamIdOrderByPlayedAtDesc(Long teamId);
}
