-- === COACH ===
CREATE TABLE if not exists coach (
                       id BIGSERIAL PRIMARY KEY,
                       first_name VARCHAR(60) NOT NULL,
                       last_name VARCHAR(60) NOT NULL,
                       created_date_time TIMESTAMP DEFAULT now() NOT NULL,
                       updated_date_time TIMESTAMP
);

CREATE INDEX idx_coach_last_name ON coach(last_name);

-- === TEAM ===
CREATE TABLE if not exists team (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(60) NOT NULL UNIQUE,
                      country VARCHAR(60),
                      coach_id BIGINT REFERENCES coach(id),
                      created_date_time TIMESTAMP DEFAULT now() NOT NULL,
                      updated_date_time TIMESTAMP
);

-- === PLAYER ===
CREATE TABLE if not exists player (
                        id BIGSERIAL PRIMARY KEY,
                        first_name VARCHAR(60) NOT NULL,
                        last_name VARCHAR(60) NOT NULL,
                        team_id BIGINT REFERENCES team(id) ON DELETE SET NULL,
                        created_date_time TIMESTAMP DEFAULT now() NOT NULL,
                        updated_date_time TIMESTAMP,
                        CONSTRAINT player_team_lastname_idx UNIQUE (team_id, last_name)
);

-- === CHAMPIONSHIP ===
CREATE TABLE if not exists championship (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(100) NOT NULL UNIQUE,
                              start_date TIMESTAMP,
                              end_date TIMESTAMP,
                              created_date_time TIMESTAMP DEFAULT now() NOT NULL,
                              updated_date_time TIMESTAMP
);

-- === MATCH ===
CREATE TABLE if not exists match (
                       id BIGSERIAL PRIMARY KEY,
                       team1_id BIGINT NOT NULL REFERENCES team(id),
                       team2_id BIGINT NOT NULL REFERENCES team(id),
                       team1_score INT NOT NULL,
                       team2_score INT NOT NULL,
                       match_date TIMESTAMP NOT NULL,
                       championship_id BIGINT REFERENCES championship(id),
                       created_date_time TIMESTAMP DEFAULT now() NOT NULL,
                       updated_date_time TIMESTAMP,
                       CONSTRAINT ux_match_teams_date UNIQUE (team1_id, team2_id, match_date)
);

CREATE INDEX idx_match_date ON match(match_date);

-- === MATCH_PLAYER ===
CREATE TABLE if not exists match_player (
                              match_id BIGINT NOT NULL REFERENCES match(id),
                              player_id BIGINT NOT NULL REFERENCES player(id),
                              team_id BIGINT NOT NULL REFERENCES team(id),
                              is_starting BOOLEAN DEFAULT TRUE NOT NULL,
                              minutes_played INT,
                              created_date_time TIMESTAMP DEFAULT now() NOT NULL,
                              updated_date_time TIMESTAMP,
                              PRIMARY KEY (match_id, player_id)
);

-- === GOALS ===
CREATE TABLE if not exists goals (
                       id BIGSERIAL PRIMARY KEY,
                       match_id BIGINT NOT NULL REFERENCES match(id),
                       player_id BIGINT NOT NULL REFERENCES player(id),
                       goal_time INT NOT NULL CHECK (goal_time BETWEEN 0 AND 120),
                       created_date_time TIMESTAMP DEFAULT now() NOT NULL,
                       updated_date_time TIMESTAMP
);

CREATE INDEX idx_goals_match_player ON goals(match_id, player_id);
