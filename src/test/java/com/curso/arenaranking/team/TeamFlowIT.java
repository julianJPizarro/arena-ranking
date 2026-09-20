package com.curso.arenaranking.team;

import com.curso.arenaranking.IntegrationTestBase;
import com.curso.arenaranking.team.dto.TeamRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TeamFlowIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void cleanDatabase() {
        teamRepository.deleteAll();
    }

    @Test
    void shouldCreateListAndGetATeam() throws Exception {
        String body = objectMapper.writeValueAsString(new TeamRequest(
                "Team Nova",
                "TNV",
                "LATAM",
                null,
                LocalDate.of(2020, 1, 15)
        ));

        String response = mockMvc.perform(post("/teams")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Team Nova")))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/teams/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tag", is("TNV")));

        mockMvc.perform(get("/teams").param("region", "LATAM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("Team Nova")));
    }

    @Test
    void shouldRejectDuplicateName() throws Exception {
        String body = objectMapper.writeValueAsString(new TeamRequest(
                "Team Duplicate", "TDP", "EU", null, null
        ));

        mockMvc.perform(post("/teams").contentType("application/json").content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/teams").contentType("application/json").content(body))
                .andExpect(status().isConflict());
    }
}
