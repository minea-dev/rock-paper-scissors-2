package com.mlicer.rockpaperscissors.domain.model;

import com.mlicer.rockpaperscissors.domain.rules.ClassicRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private Player player1;
    private Player player2;
    private Game game;

    @BeforeEach
    void setUp() {
        player1 = new Player("1", "Alice");
        player2 = new Player("2", "Bob");
        game = new Game("game-123", player1, player2, new ClassicRules(), WinningCondition.bestOf(5));
    }

    @Test
    void player1ShouldScorePointWhenWinningRound() {
        game.playRound(player1, Move.ROCK);
        game.playRound(player2, Move.SCISSORS);

        assertEquals(1, game.getScorePlayer1());
        assertEquals(0, game.getScorePlayer2());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    void nobodyShouldScoreOnDraw() {
        game.playRound(player1, Move.ROCK);
        game.playRound(player2, Move.ROCK);

        assertEquals(0, game.getScorePlayer1());
        assertEquals(0, game.getScorePlayer2());
    }

    @Test
    void gameShouldFinishWhenAPlayerReachesThreePoints() {
        // Round 1: Wins P1
        game.playRound(player1, Move.ROCK);
        game.playRound(player2, Move.SCISSORS);

        // Round 2: Wins P1
        game.playRound(player1, Move.ROCK);
        game.playRound(player2, Move.SCISSORS);

        // Round 3: Wins P1
        game.playRound(player1, Move.ROCK);
        game.playRound(player2, Move.SCISSORS);

        assertEquals(3, game.getScorePlayer1());
        assertEquals(GameStatus.FINISHED, game.getStatus());
    }

    @Test
    void shouldThrowExceptionIfGameIsAlreadyFinished() {
        // Force 3 quick wins for player1
        for (int i = 0; i < 3; i++) {
            game.playRound(player1, Move.ROCK);
            game.playRound(player2, Move.SCISSORS);
        }

        // Attempting to play a fourth round should fail
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> game.playRound(player1, Move.PAPER)
        );
        assertEquals("The game has already finished.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfMoveIsNotAllowedInCurrentRules() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> game.playRound(player1, Move.SPOCK)
        );
        assertEquals("Move SPOCK is not allowed in this game mode.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfUnknownPlayerTriesToPlay() {
        Player hacker = new Player("99", "Hacker");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> game.playRound(hacker, Move.ROCK)
        );
        assertEquals("Player does not belong to this game.", exception.getMessage());
    }

    @Test
    void player2ShouldScorePointWhenWinningRound() {
        game.playRound(player1, Move.SCISSORS);
        game.playRound(player2, Move.ROCK);

        assertEquals(0, game.getScorePlayer1());
        assertEquals(1, game.getScorePlayer2());
    }

    @Test
    void shouldThrowExceptionWhenPlayersAreTheSame() {
        assertThrows(IllegalArgumentException.class, () ->
                new Game("game-1", player1, player1, new ClassicRules(), WinningCondition.bestOf(5))
        );
    }

    @Test
    void shouldThrowExceptionIfSamePlayerTriesToPlayTwiceInSameRound() {
        game.playRound(player1, Move.ROCK);
        assertThrows(IllegalStateException.class, () -> game.playRound(player1, Move.PAPER));
    }

    @Test
    void gameShouldFinishWithPlayer2AsWinnerWhenReachingThreePoints() {
        for (int i = 0; i < 3; i++) {
            game.playRound(player1, Move.SCISSORS);
            game.playRound(player2, Move.ROCK);
        }

        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertEquals(player2, game.getWinner().orElse(null));
    }

    @Test
    void shouldThrowExceptionWhenGameIdOrRulesAreNull() {
        assertThrows(IllegalArgumentException.class, () -> new Game(null, player1, player2, new ClassicRules(), WinningCondition.bestOf(5)));
        assertThrows(IllegalArgumentException.class, () -> new Game("game-1", player1, player2, null, WinningCondition.bestOf(5)));
        assertThrows(IllegalArgumentException.class, () -> new Game("game-1", player1, player2, new ClassicRules(), null));
    }

    @Test
    void shouldThrowExceptionWhenPlayerOrMoveIsNullInPlayRound() {
        assertThrows(IllegalArgumentException.class, () -> game.playRound(null, Move.ROCK));
        assertThrows(IllegalArgumentException.class, () -> game.playRound(player1, null));
    }

    @Test
    void winnerShouldBeEmptyWhileGameIsInProgress() {
        assertTrue(game.getWinner().isEmpty());

        game.playRound(player1, Move.ROCK);
        game.playRound(player2, Move.SCISSORS);

        assertTrue(game.getWinner().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenPlayersAreNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Game("game-1", null, player2, new ClassicRules(), WinningCondition.bestOf(5))
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Game("game-1", player1, null, new ClassicRules(), WinningCondition.bestOf(5))
        );
    }

    @Test
    void shouldRecordRoundHistoryCorrectly() {
        game.playRound(player1, Move.ROCK);
        game.playRound(player2, Move.SCISSORS);

        game.playRound(player1, Move.PAPER);
        game.playRound(player2, Move.PAPER);

        List<Round> history = game.getRoundsHistory();

        assertEquals(2, history.size());

        Round firstRound = history.get(0);
        assertEquals(1, firstRound.roundNumber());
        assertEquals(Move.ROCK, firstRound.player1Move());
        assertEquals(Move.SCISSORS, firstRound.player2Move());
        assertEquals(Result.WIN, firstRound.result());

        Round secondRound = history.get(1);
        assertEquals(2, secondRound.roundNumber());
        assertEquals(Move.PAPER, secondRound.player1Move());
        assertEquals(Move.PAPER, secondRound.player2Move());
        assertEquals(Result.DRAW, secondRound.result());
    }
}
