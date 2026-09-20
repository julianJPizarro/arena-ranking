package com.curso.arenaranking.team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Page<Team> findByRegionIgnoreCase(String region, Pageable pageable);

    Optional<Team> findByNameIgnoreCase(String name);
}
