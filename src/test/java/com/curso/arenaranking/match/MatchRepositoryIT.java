package com.curso.arenaranking.match;

import com.curso.arenaranking.PostgresContainerSupport;
import com.curso.arenaranking.team.Team;
import com.curso.arenaranking.team.TeamRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class MatchRepositoryIT extends PostgresContainerSupport {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    private Team team;

    @BeforeEach
    void setUp() {
        matchRepository.deleteAll();
        teamRepository.deleteAll();
        team = teamRepository.save(Team.builder()
                .name("Team Vortex")
                .tag("TVX")
                .region("LATAM")
                .build());
    }

    private Match newMatch(String opponent, String tournament, int teamScore, int opponentScore, LocalDateTime playedAt) {
        return Match.builder()
                .team(team)
                .opponent(opponent)
                .tournament(tournament)
                .teamScore(teamScore)
                .opponentScore(opponentScore)
                .playedAt(playedAt)
                .build();
    }

    @Test
    void shouldFindMatchesByTeamIdPaginated() {
        matchRepository.save(newMatch("Rival A", "Cup2026", 2, 1, LocalDateTime.now().minusDays(1)));
        matchRepository.save(newMatch("Rival B", "Cup2026", 1, 1, LocalDateTime.now()));

        Page<Match> page = matchRepository.findByTeamId(team.getId(), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldFilterMatchesByTournamentIgnoringCase() {
        matchRepository.save(newMatch("Rival A", "Cup2026", 2, 1, LocalDateTime.now().minusDays(1)));
        matchRepository.save(newMatch("Rival B", "League", 1, 1, LocalDateTime.now()));

        Page<Match> page = matchRepository.findByTeamIdAndTournamentIgnoreCase(
                team.getId(), "cup2026", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getOpponent()).isEqualTo("Rival A");
    }

    @Test
    void shouldCountMatchesByTeamIdAndResult() {
        matchRepository.save(newMatch("Rival A", "Cup2026", 3, 0, LocalDateTime.now().minusDays(2)));
        matchRepository.save(newMatch("Rival B", "Cup2026", 0, 2, LocalDateTime.now().minusDays(1)));
        matchRepository.save(newMatch("Rival C", "Cup2026", 1, 1, LocalDateTime.now()));

        assertThat(matchRepository.countByTeamId(team.getId())).isEqualTo(3);
        assertThat(matchRepository.countByTeamIdAndResult(team.getId(), MatchResult.WIN)).isEqualTo(1);
        assertThat(matchRepository.countByTeamIdAndResult(team.getId(), MatchResult.LOSS)).isEqualTo(1);
        assertThat(matchRepository.countByTeamIdAndResult(team.getId(), MatchResult.DRAW)).isEqualTo(1);
    }

    @Test
    void shouldOrderMatchesByPlayedAtDescending() {
        matchRepository.save(newMatch("Rival A", "Cup2026", 1, 0, LocalDateTime.now().minusDays(2)));
        matchRepository.save(newMatch("Rival B", "Cup2026", 1, 0, LocalDateTime.now()));
        matchRepository.save(newMatch("Rival C", "Cup2026", 1, 0, LocalDateTime.now().minusDays(1)));

        List<Match> matches = matchRepository.findByTeamIdOrderByPlayedAtDesc(team.getId());

        assertThat(matches).extracting(Match::getOpponent)
                .containsExactly("Rival B", "Rival C", "Rival A");
    }
}
