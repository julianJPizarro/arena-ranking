package com.curso.arenaranking.match;

import com.curso.arenaranking.IntegrationTestBase;
import com.curso.arenaranking.match.dto.MatchRequest;
import com.curso.arenaranking.team.TeamRepository;
import com.curso.arenaranking.team.dto.TeamRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MatchStatsFlowIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void cleanDatabase() {
        matchRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @Test
    void shouldRegisterMatchesAndComputeStatsAndStreak() throws Exception {
        Long teamId = createTeam("Team Vortex", "TVX");

        registerMatch(teamId, "Rival A", "Cup2026", 3, 1, LocalDateTime.now().minusDays(3));
        registerMatch(teamId, "Rival B", "Cup2026", 2, 0, LocalDateTime.now().minusDays(2));
        registerMatch(teamId, "Rival C", "League", 0, 1, LocalDateTime.now().minusDays(1));
        registerMatch(teamId, "Rival D", "League", 4, 2, LocalDateTime.now());

        mockMvc.perform(get("/teams/{id}/matches", teamId).param("tournament", "League"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(2)));

        mockMvc.perform(get("/teams/{id}/stats", teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMatches", is(4)))
                .andExpect(jsonPath("$.wins", is(3)))
                .andExpect(jsonPath("$.losses", is(1)))
                .andExpect(jsonPath("$.winRate", is(75.0)));

        mockMvc.perform(get("/teams/{id}/streak", teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak", is(1)));
    }

    private Long createTeam(String name, String tag) throws Exception {
        String body = objectMapper.writeValueAsString(new TeamRequest(name, tag, "LATAM", null, null));
        String response = mockMvc.perform(post("/teams").contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private void registerMatch(Long teamId, String opponent, String tournament, int teamScore,
                                int opponentScore, LocalDateTime playedAt) throws Exception {
        String body = objectMapper.writeValueAsString(
                new MatchRequest(opponent, tournament, teamScore, opponentScore, playedAt));
        mockMvc.perform(post("/teams/{id}/matches", teamId).contentType("application/json").content(body))
                .andExpect(status().isCreated());
    }
}
