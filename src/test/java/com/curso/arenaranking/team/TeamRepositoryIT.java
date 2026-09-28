package com.curso.arenaranking.team;

import com.curso.arenaranking.PostgresContainerSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
/*import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
*/
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/*@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
*/
class TeamRepositoryIT extends PostgresContainerSupport {

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void cleanDatabase() {
        teamRepository.deleteAll();
    }

    @Test
    void shouldFindTeamByNameIgnoringCase() {
        teamRepository.save(Team.builder()
                .name("Team Vortex")
                .tag("TVX")
                .region("LATAM")
                .build());

        Optional<Team> found = teamRepository.findByNameIgnoreCase("team vortex");

        assertThat(found).isPresent();
        assertThat(found.get().getTag()).isEqualTo("TVX");
    }

    @Test
    void shouldReturnEmptyWhenNameDoesNotExist() {
        Optional<Team> found = teamRepository.findByNameIgnoreCase("Unknown Team");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldFilterTeamsByRegionIgnoringCaseAndPaginate() {
        teamRepository.save(Team.builder().name("Team A").tag("TA").region("LATAM").build());
        teamRepository.save(Team.builder().name("Team B").tag("TB").region("latam").build());
        teamRepository.save(Team.builder().name("Team C").tag("TC").region("EU").build());

        Page<Team> page = teamRepository.findByRegionIgnoreCase("LATAM", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent())
                .extracting(Team::getName)
                .containsExactlyInAnyOrder("Team A", "Team B");
    }

    @Test
    void shouldEnforceUniqueNameConstraint() {
        teamRepository.save(Team.builder().name("Team Repository Duplicate").tag("TD1").region("EU").build());
        teamRepository.flush();

        Team duplicate = Team.builder().name("Team Repository Duplicate").tag("TD2").region("EU").build();

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> {
                    teamRepository.save(duplicate);
                    teamRepository.flush();
                });
    }
}
