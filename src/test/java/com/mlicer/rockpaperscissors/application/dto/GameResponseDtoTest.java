package com.mlicer.rockpaperscissors.application.dto;

import com.mlicer.rockpaperscissors.domain.model.*;
import com.mlicer.rockpaperscissors.domain.rules.ClassicRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameResponseDtoTest {

    private Player player1;
    private Player player2;
    private Game game;

    @BeforeEach
    void setUp() {
        player1 = new Player("user-1", "Alice");
        player2 = new Player("user-2", "Bob");
        game = new Game("game-100", player1, player2, new ClassicRules(), WinningCondition.bestOf(3));
    }

    @Test
    void shouldReportOpponentMovedCorrectlyWithoutLeakingMoveValue() {
        game.playRound(player1, Move.ROCK);

        GameResponseDto p1View = GameResponseDto.from(game, player1.id());
        assertTrue(p1View.hasUserMoved());
        assertFalse(p1View.hasOpponentMoved());
        assertTrue(p1View.history().isEmpty());

        GameResponseDto p2View = GameResponseDto.from(game, player2.id());
        assertFalse(p2View.hasUserMoved());
        assertTrue(p2View.hasOpponentMoved());
        assertTrue(p2View.history().isEmpty());
    }

    @Test
    void shouldRevealMovesInHistoryOnlyAfterRoundIsEvaluated() {
        game.playRound(player1, Move.ROCK);
        game.playRound(player2, Move.SCISSORS);

        GameResponseDto p1View = GameResponseDto.from(game, player1.id());
        GameResponseDto p2View = GameResponseDto.from(game, player2.id());

        assertFalse(p1View.hasUserMoved());
        assertFalse(p1View.hasOpponentMoved());
        assertFalse(p2View.hasUserMoved());
        assertFalse(p2View.hasOpponentMoved());

        assertEquals(1, p1View.history().size());
        RoundResponseDto round = p1View.history().get(0);
        assertEquals("ROCK", round.player1Move());
        assertEquals("SCISSORS", round.player2Move());
        assertEquals("WIN", round.result());
    }
}