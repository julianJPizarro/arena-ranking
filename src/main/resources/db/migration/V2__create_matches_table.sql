CREATE TABLE matches (
    id              BIGSERIAL PRIMARY KEY,
    team_id         BIGINT       NOT NULL,
    opponent        VARCHAR(100) NOT NULL,
    tournament      VARCHAR(100),
    team_score      INT          NOT NULL,
    opponent_score  INT          NOT NULL,
    result          VARCHAR(10)  NOT NULL,
    played_at       TIMESTAMP    NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_matches_team FOREIGN KEY (team_id)
        REFERENCES teams (id) ON DELETE CASCADE
);

CREATE INDEX idx_matches_team_id ON matches (team_id);
CREATE INDEX idx_matches_tournament ON matches (tournament);
CREATE INDEX idx_matches_played_at ON matches (played_at);
