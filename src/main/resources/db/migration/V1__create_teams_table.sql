CREATE TABLE teams (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    tag             VARCHAR(10)  NOT NULL,
    region          VARCHAR(50),
    logo_url        VARCHAR(255),
    founded_date    DATE,
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT uq_teams_name UNIQUE (name)
);

CREATE INDEX idx_teams_region ON teams (region);
