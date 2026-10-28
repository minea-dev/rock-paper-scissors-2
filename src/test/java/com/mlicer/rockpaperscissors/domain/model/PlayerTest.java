package com.mlicer.rockpaperscissors.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerTest {

    @Test
    void shouldCreatePlayerWhenDataIsValid() {
        assertDoesNotThrow(() -> new Player("1", "Alice"));
    }

    @Test
    void shouldThrowExceptionWhenPlayerIdIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Player("", "Alice"));
        assertThrows(IllegalArgumentException.class, () -> new Player("   ", "Alice"));
    }

    @Test
    void shouldThrowExceptionWhenPlayerIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Player(null, "Alice"));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Player("1", ""));
        assertThrows(IllegalArgumentException.class, () -> new Player("1", "   "));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Player("1", null));
    }
}
