package com.mlicer.rockpaperscissors.domain.model;

import com.mlicer.rockpaperscissors.domain.rules.GameRules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Game {
    private final String id;
    private final Player player1;
    private final Player player2;
    private final GameRules rules;
    private final WinningCondition winningCondition;

    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;

    private Move movePlayer1;
    private Move movePlayer2;

    private GameStatus status;

    private final List<Round> rounds = new ArrayList<>();

    public Game(String id, Player player1, Player player2, GameRules rules, WinningCondition winningCondition) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Game ID cannot be null or blank.");
        if (player1 == null || player2 == null) throw new IllegalArgumentException("Players cannot be null.");
        if (player1.equals(player2)) throw new IllegalArgumentException("A player cannot play against themselves.");
        if (rules == null) throw new IllegalArgumentException("Rules cannot be null.");
        if (winningCondition == null) throw new IllegalArgumentException("Winning condition cannot be null.");

        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.rules = rules;
        this.winningCondition = winningCondition;
        this.status = GameStatus.IN_PROGRESS;
        this.scorePlayer1 = 0;
        this.scorePlayer2 = 0;
    }

    public void playRound(Player player, Move move) {
        if (player == null || move == null) {
            throw new IllegalArgumentException("Player and Move cannot be null.");
        }
        if (this.status == GameStatus.FINISHED) {
            throw new IllegalStateException("The game has already finished.");
        }
        if (this.status == GameStatus.ABANDONED) {
            throw new IllegalStateException("The game was abandoned.");
        }
        if (!rules.allowedMoves().contains(move)) {
            throw new IllegalArgumentException("Move " + move + " is not allowed in this game mode.");
        }

        assignMove(player, move);

        if (movePlayer1 != null && movePlayer2 != null) {
            evaluateRound();
        }
    }

    private void assignMove(Player player, Move move) {
        if (player.equals(player1)) {
            if (this.movePlayer1 != null) throw new IllegalStateException("Player 1 has already submitted a move for this round.");
            this.movePlayer1 = move;
        } else if (player.equals(player2)) {
            if (this.movePlayer2 != null) throw new IllegalStateException("Player 2 has already submitted a move for this round.");
            this.movePlayer2 = move;
        } else {
            throw new IllegalArgumentException("Player does not belong to this game.");
        }
    }

    private void evaluateRound() {
        Result result = rules.evaluate(movePlayer1, movePlayer2);

        if (result == Result.WIN) {
            scorePlayer1++;
        } else if (result == Result.LOSE) {
            scorePlayer2++;
        }

        rounds.add(new Round(rounds.size() + 1, movePlayer1, movePlayer2, result));

        resetMovesForNextRound();
        checkIfGameFinished();
    }

    private void resetMovesForNextRound() {
        this.movePlayer1 = null;
        this.movePlayer2 = null;
    }

    private void checkIfGameFinished() {
        if (scorePlayer1 >= winningCondition.targetScore() || scorePlayer2 >= winningCondition.targetScore()) {
            this.status = GameStatus.FINISHED;
        }
    }

    public Optional<Player> getWinner() {
        if (scorePlayer1 >= winningCondition.targetScore()) return Optional.of(player1);
        if (scorePlayer2 >= winningCondition.targetScore()) return Optional.of(player2);
        return Optional.empty();
    }

    public List<Round> getRoundsHistory() {
        return Collections.unmodifiableList(rounds);
    }

    public String getId() { return id; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public int getScorePlayer1() { return scorePlayer1; }
    public int getScorePlayer2() { return scorePlayer2; }
    public GameStatus getStatus() { return status; }
}