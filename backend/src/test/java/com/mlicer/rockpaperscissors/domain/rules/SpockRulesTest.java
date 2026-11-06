package com.mlicer.rockpaperscissors.domain.rules;

import com.mlicer.rockpaperscissors.domain.model.Move;
import com.mlicer.rockpaperscissors.domain.model.Result;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpockRulesTest {

    private final SpockRules rules = new SpockRules();

    @Test
    void shouldEvaluateSpockSpecialWinsCorrectly() {
        // Spock smashes Scissors
        assertEquals(Result.WIN, rules.evaluate(Move.SPOCK, Move.SCISSORS));
        // Lizard poisons Spock
        assertEquals(Result.WIN, rules.evaluate(Move.LIZARD, Move.SPOCK));
        // Paper disproves Spock
        assertEquals(Result.WIN, rules.evaluate(Move.PAPER, Move.SPOCK));
    }

    @Test
    void allowedMovesShouldContainAllFiveOptions() {
        SpockRules spockRules = new SpockRules();
        assertTrue(spockRules.allowedMoves().containsAll(
                Set.of(Move.ROCK, Move.PAPER, Move.SCISSORS, Move.LIZARD, Move.SPOCK)
        ));
    }

    @Test
    void shouldReturnDrawWhenSpockMovesAreIdentical() {
        SpockRules spockRules = new SpockRules();
        assertEquals(Result.DRAW, spockRules.evaluate(Move.SPOCK, Move.SPOCK));
        assertEquals(Result.DRAW, spockRules.evaluate(Move.LIZARD, Move.LIZARD));
    }

    @Test
    void shouldEvaluateLosesCorrectlyInSpockRules() {
        SpockRules spockRules = new SpockRules();
        assertEquals(Result.LOSE, spockRules.evaluate(Move.SPOCK, Move.PAPER));
        assertEquals(Result.LOSE, spockRules.evaluate(Move.LIZARD, Move.ROCK));
    }
}
