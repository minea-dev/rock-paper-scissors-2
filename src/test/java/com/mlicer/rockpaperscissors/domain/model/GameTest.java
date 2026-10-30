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

    @Test
    void shouldCreateMultiplayerGameWaitingForPlayer2() {
        Game multiplayerGame = new Game("room-123", player1, new ClassicRules(), WinningCondition.bestOf(3));

        assertEquals(GameStatus.WAITING_FOR_PLAYER, multiplayerGame.getStatus());
        assertNull(multiplayerGame.getPlayer2());
        assertEquals(player1, multiplayerGame.getPlayer1());
    }

    @Test
    void shouldAllowGuestPlayerToJoinWaitingGame() {
        Game waitingGame = new Game("room-100", player1, new ClassicRules(), WinningCondition.bestOf(3));
        assertEquals(GameStatus.WAITING_FOR_PLAYER, waitingGame.getStatus());
        assertNull(waitingGame.getPlayer2());

        waitingGame.join(player2);

        assertEquals(GameStatus.IN_PROGRESS, waitingGame.getStatus());
        assertEquals(player2, waitingGame.getPlayer2());
    }

    @Test
    void shouldThrowExceptionWhenGuestIsNull() {
        Game waitingGame = new Game("room-100", player1, new ClassicRules(), WinningCondition.bestOf(3));
        assertThrows(IllegalArgumentException.class, () -> waitingGame.join(null));
    }

    @Test
    void shouldThrowExceptionWhenHostTriesToJoinThemselves() {
        Game waitingGame = new Game("room-100", player1, new ClassicRules(), WinningCondition.bestOf(3));
        assertThrows(IllegalArgumentException.class, () -> waitingGame.join(player1));
    }

    @Test
    void shouldThrowExceptionWhenThirdPlayerTriesToJoinFullGame() {
        Player player3 = new Player("3", "Charlie");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> game.join(player3)
        );
        assertEquals("Game is already full or not open for joining.", exception.getMessage());
    }

    @Test
    void shouldAllowPlayerToAbandonAndDeclareOpponentAsWinner() {
        Game inProgressGame = new Game("room-123", player1, player2, new ClassicRules(), WinningCondition.bestOf(3));
        assertEquals(GameStatus.IN_PROGRESS, inProgressGame.getStatus());

        inProgressGame.abandon(player1);

        assertEquals(GameStatus.ABANDONED, inProgressGame.getStatus());
        assertTrue(inProgressGame.getWinner().isPresent());
        assertEquals(player2, inProgressGame.getWinner().get());
    }

    @Test
    void shouldThrowExceptionWhenNullPlayerTriesToAbandon() {
        assertThrows(IllegalArgumentException.class, () -> game.abandon(null));
    }

    @Test
    void shouldThrowExceptionWhenTryingToAbandonAFinishedGame() {
        for (int i = 0; i < 3; i++) {
            game.playRound(player1, Move.ROCK);
            game.playRound(player2, Move.SCISSORS);
        }
        assertEquals(GameStatus.FINISHED, game.getStatus());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> game.abandon(player1)
        );
        assertEquals("Cannot abandon a game that is already finished or abandoned.", exception.getMessage());
    }

    @Test
    void shouldHideMoveValueButConfirmPlayerHasMoved() {
        Game inProgressGame = new Game("room-123", player1, player2, new ClassicRules(), WinningCondition.bestOf(3));

        assertFalse(inProgressGame.hasPlayerMoved(player1));
        assertFalse(inProgressGame.hasPlayerMoved(player2));

        inProgressGame.playRound(player1, Move.ROCK);

        assertTrue(inProgressGame.hasPlayerMoved(player1));
        assertFalse(inProgressGame.hasPlayerMoved(player2));
    }

    @Test
    void shouldThrowExceptionWhenCheckingMoveForUnknownPlayer() {
        Player unknown = new Player("99", "Hacker");
        assertThrows(IllegalArgumentException.class, () -> game.hasPlayerMoved(unknown));
    }
}
