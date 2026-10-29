package com.mlicer.rockpaperscissors.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WinningConditionTest {

    @Test
    void shouldCreateBestOfConditionCorrectly() {
        WinningCondition condition = WinningCondition.bestOf(5);
        assertEquals(5, condition.totalRounds());
        assertEquals(3, condition.targetScore());
    }

    @Test
    void shouldThrowExceptionIfRoundsAreEvenOrInvalid() {
        assertThrows(IllegalArgumentException.class, () -> WinningCondition.bestOf(0));
        assertThrows(IllegalArgumentException.class, () -> WinningCondition.bestOf(4));
    }
}