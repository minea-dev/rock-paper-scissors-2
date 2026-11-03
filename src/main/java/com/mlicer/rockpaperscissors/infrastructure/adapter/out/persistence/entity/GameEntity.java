package com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
public class GameEntity {
    @Id
    private String id;

    @Column(name = "player1_id", nullable = false)
    private String player1Id;

    @Column(name = "player1_username", nullable = false)
    private String player1Username;

    @Column(name = "player2_id")
    private String player2Id;

    @Column(name = "player2_username")
    private String player2Username;

    @Column(name = "rules_type", nullable = false)
    private String rulesType;

    @Column(name = "winning_condition_total_rounds", nullable = false)
    private int winningConditionTotalRounds;

    @Column(name = "winning_condition_target_score", nullable = false)
    private int winningConditionTargetScore;

    @Column(name = "score_player1", nullable = false)
    private int scorePlayer1;

    @Column(name = "score_player2", nullable = false)
    private int scorePlayer2;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "current_move_player1")
    private String currentMovePlayer1;

    @Column(name = "current_move_player2")
    private String currentMovePlayer2;

    @Column(name = "player_who_abandoned_id")
    private String playerWhoAbandonedId;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RoundEntity> rounds = new ArrayList<>();

    protected GameEntity() {}

    public GameEntity(String id) {
        this.id = id;
    }

    public void addRound(RoundEntity round) {
        rounds.add(round);
    }

    public void clearRounds() {
        rounds.clear();
    }

    public String getId() { return id; }
    public String getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(String player1Id) { this.player1Id = player1Id; }
    public String getPlayer1Username() { return player1Username; }
    public void setPlayer1Username(String player1Username) { this.player1Username = player1Username; }
    public String getPlayer2Id() { return player2Id; }
    public void setPlayer2Id(String player2Id) { this.player2Id = player2Id; }
    public String getPlayer2Username() { return player2Username; }
    public void setPlayer2Username(String player2Username) { this.player2Username = player2Username; }
    public String getRulesType() { return rulesType; }
    public void setRulesType(String rulesType) { this.rulesType = rulesType; }
    public int getWinningConditionTotalRounds() { return winningConditionTotalRounds; }
    public void setWinningConditionTotalRounds(int winningConditionTotalRounds) { this.winningConditionTotalRounds = winningConditionTotalRounds; }
    public int getWinningConditionTargetScore() { return winningConditionTargetScore; }
    public void setWinningConditionTargetScore(int winningConditionTargetScore) { this.winningConditionTargetScore = winningConditionTargetScore; }
    public int getScorePlayer1() { return scorePlayer1; }
    public void setScorePlayer1(int scorePlayer1) { this.scorePlayer1 = scorePlayer1; }
    public int getScorePlayer2() { return scorePlayer2; }
    public void setScorePlayer2(int scorePlayer2) { this.scorePlayer2 = scorePlayer2; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPlayerWhoAbandonedId() { return playerWhoAbandonedId; }
    public void setPlayerWhoAbandonedId(String playerWhoAbandonedId) { this.playerWhoAbandonedId = playerWhoAbandonedId; }
    public List<RoundEntity> getRounds() { return rounds; }
    public String getCurrentMovePlayer1() { return currentMovePlayer1; }
    public void setCurrentMovePlayer1(String currentMovePlayer1) { this.currentMovePlayer1 = currentMovePlayer1; }
    public String getCurrentMovePlayer2() { return currentMovePlayer2; }
    public void setCurrentMovePlayer2(String currentMovePlayer2) { this.currentMovePlayer2 = currentMovePlayer2; }
}