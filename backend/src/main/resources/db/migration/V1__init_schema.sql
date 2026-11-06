CREATE TABLE games (
                       id VARCHAR(36) PRIMARY KEY,

                       player1_id VARCHAR(50) NOT NULL,
                       player1_username VARCHAR(100) NOT NULL,

                       player2_id VARCHAR(50),
                       player2_username VARCHAR(100),

                       rules_type VARCHAR(20) NOT NULL,
                       winning_condition_total_rounds INT NOT NULL,
                       winning_condition_target_score INT NOT NULL,

                       score_player1 INT NOT NULL DEFAULT 0,
                       score_player2 INT NOT NULL DEFAULT 0,

                       current_move_player1 VARCHAR(20),
                       current_move_player2 VARCHAR(20),

                       status VARCHAR(20) NOT NULL,

                       player_who_abandoned_id VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rounds (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        game_id VARCHAR(36) NOT NULL,
                        round_number INT NOT NULL,
                        player1_move VARCHAR(20) NOT NULL,
                        player2_move VARCHAR(20) NOT NULL,
                        result VARCHAR(20) NOT NULL,

                        CONSTRAINT fk_rounds_game_id FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_rounds_game_id ON rounds(game_id);