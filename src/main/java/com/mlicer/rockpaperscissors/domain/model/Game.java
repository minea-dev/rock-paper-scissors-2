package com.mlicer.rockpaperscissors.domain.model;

import com.mlicer.rockpaperscissors.domain.exception.GameRuleViolationException;
import com.mlicer.rockpaperscissors.domain.exception.InvalidMoveException;
import com.mlicer.rockpaperscissors.domain.rules.GameRules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Game {
    private final String id;
    private final Player player1;
    private Player player2;
    private final GameRules rules;
    private final WinningCondition winningCondition;

    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;

    private Move movePlayer1;
    private Move movePlayer2;

    private Player playerWhoAbandoned;

    private GameStatus status;

    private final List<Round> rounds = new ArrayList<>();

    public Game(String id, Player player1, Player player2, GameRules rules, WinningCondition winningCondition) {
        if (id == null || id.isBlank()) {
            throw new GameRuleViolationException("Game ID cannot be null or blank.");
        }
        if (player1 == null || player2 == null) {
            throw new GameRuleViolationException("Players cannot be null.");
        }
        if (player1.equals(player2)) {
            throw new GameRuleViolationException("A player cannot play against themselves.");
        }
        if (rules == null) {
            throw new GameRuleViolationException("Rules cannot be null.");
        }
        if (winningCondition == null) {
            throw new GameRuleViolationException("Winning condition cannot be null.");
        }

        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.rules = rules;
        this.winningCondition = winningCondition;
        this.status = GameStatus.IN_PROGRESS;
        this.scorePlayer1 = 0;
        this.scorePlayer2 = 0;
    }

    public Game(String id, Player player1, GameRules rules, WinningCondition winningCondition) {
        if (id == null || id.isBlank()) {
            throw new GameRuleViolationException("Game ID cannot be null or blank.");
        }
        if (player1 == null) {
            throw new GameRuleViolationException("Player 1 (host) cannot be null.");
        }
        if (rules == null) {
            throw new GameRuleViolationException("Rules cannot be null.");
        }
        if (winningCondition == null) {
            throw new GameRuleViolationException("Winning condition cannot be null.");
        }

        this.id = id;
        this.player1 = player1;
        this.player2 = null;
        this.rules = rules;
        this.winningCondition = winningCondition;
        this.status = GameStatus.WAITING_FOR_PLAYER;
        this.scorePlayer1 = 0;
        this.scorePlayer2 = 0;
    }

    public void playRound(Player player, Move move) {
        if (this.status == GameStatus.WAITING_FOR_PLAYER) {
            throw new GameRuleViolationException("Cannot play yet, waiting for player 2 to join.");
        }
        if (player == null || move == null) {
            throw new GameRuleViolationException("Player and Move cannot be null.");
        }
        if (this.status == GameStatus.FINISHED) {
            throw new GameRuleViolationException("The game has already finished.");
        }
        if (this.status == GameStatus.ABANDONED) {
            throw new GameRuleViolationException("The game was abandoned.");
        }
        if (!rules.allowedMoves().contains(move)) {
            throw new InvalidMoveException("Move " + move + " is not allowed in this game mode.");
        }

        assignMove(player, move);

        if (movePlayer1 != null && movePlayer2 != null) {
            evaluateRound();
        }
    }

    public boolean hasPlayerMoved(Player player) {
        if (player == null) {
            throw new GameRuleViolationException("Player cannot be null.");
        }

        if (player.equals(player1)) {
            return this.movePlayer1 != null;
        } else if (player.equals(player2)) {
            return this.movePlayer2 != null;
        } else {
            throw new GameRuleViolationException("Player does not belong to this game.");
        }
    }

    private void assignMove(Player player, Move move) {
        if (player.equals(player1)) {
            if (this.movePlayer1 != null) {
                throw new GameRuleViolationException("Player 1 has already submitted a move for this round.");
            }
            this.movePlayer1 = move;
        } else if (player.equals(player2)) {
            if (this.movePlayer2 != null) {
                throw new GameRuleViolationException("Player 2 has already submitted a move for this round.");
            }
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
        if (this.status == GameStatus.ABANDONED && playerWhoAbandoned != null) {
            return playerWhoAbandoned.equals(player1) ? Optional.of(player2) : Optional.of(player1);
        }

        if (scorePlayer1 >= winningCondition.targetScore()) {
            return Optional.of(player1);
        }
        if (scorePlayer2 >= winningCondition.targetScore()) {
            return Optional.of(player2);
        }
        return Optional.empty();
    }

    public void join(Player guest) {
        if (guest == null) {
            throw new GameRuleViolationException("Guest player cannot be null.");
        }
        if (this.status != GameStatus.WAITING_FOR_PLAYER || this.player2 != null) {
            throw new GameRuleViolationException("Game is already full or not open for joining.");
        }
        if (guest.equals(this.player1)) {
            throw new GameRuleViolationException("A player cannot play against themselves.");
        }

        this.player2 = guest;
        this.status = GameStatus.IN_PROGRESS;
    }

    public void abandon(Player player) {
        if (player == null) {
            throw new GameRuleViolationException("Player cannot be null.");
        }
        if (this.status == GameStatus.FINISHED || this.status == GameStatus.ABANDONED) {
            throw new GameRuleViolationException("Cannot abandon a game that is already finished or abandoned.");
        }
        if (!player.equals(this.player1) && !player.equals(this.player2)) {
            throw new GameRuleViolationException("Player does not belong to this game.");
        }

        this.playerWhoAbandoned = player;
        this.status = GameStatus.ABANDONED;
    }

    public GameRules getRules() {
        return rules;
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