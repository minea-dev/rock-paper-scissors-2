package com.mlicer.rockpaperscissors.domain.rules;

import com.mlicer.rockpaperscissors.domain.model.Move;
import com.mlicer.rockpaperscissors.domain.model.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClassicRulesTest {
    private final ClassicRules rules = new ClassicRules();

    @Test
    void shouldReturnDrawWhenMovesAreTheSame() {
        assertEquals(Result.DRAW, rules.evaluate(Move.ROCK, Move.ROCK));
    }

    @Test
    void shouldEvaluateClassicWinsCorrectly() {
        assertEquals(Result.WIN, rules.evaluate(Move.ROCK, Move.SCISSORS));
        assertEquals(Result.WIN, rules.evaluate(Move.PAPER, Move.ROCK));
        assertEquals(Result.WIN, rules.evaluate(Move.SCISSORS, Move.PAPER));
    }

    @Test
    void shouldEvaluateClassicLosesCorrectly() {
        assertEquals(Result.LOSE, rules.evaluate(Move.SCISSORS, Move.ROCK));
        assertEquals(Result.LOSE, rules.evaluate(Move.ROCK, Move.PAPER));
        assertEquals(Result.LOSE, rules.evaluate(Move.PAPER, Move.SCISSORS));
    }

    @Test
    void shouldThrowExceptionWhenUnsupportedMoveIsPlayed() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> rules.evaluate(Move.SPOCK, Move.ROCK)
        );
        assertEquals("Move not supported in Classic mode", exception.getMessage());
    }

    @Test
    void allowedMovesShouldOnlyContainClassicOptions() {
        assertTrue(rules.allowedMoves().contains(Move.ROCK));
        assertTrue(rules.allowedMoves().contains(Move.PAPER));
        assertTrue(rules.allowedMoves().contains(Move.SCISSORS));
        assertFalse(rules.allowedMoves().contains(Move.SPOCK));
    }
}
