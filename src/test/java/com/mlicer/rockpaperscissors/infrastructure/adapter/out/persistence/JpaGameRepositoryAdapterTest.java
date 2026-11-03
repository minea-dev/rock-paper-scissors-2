package com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence;

import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.Move;
import com.mlicer.rockpaperscissors.domain.model.Player;
import com.mlicer.rockpaperscissors.domain.model.WinningCondition;
import com.mlicer.rockpaperscissors.domain.rules.ClassicRules;
import com.mlicer.rockpaperscissors.infrastructure.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpaGameRepositoryAdapterTest extends AbstractIntegrationTest {

    @Autowired
    private JpaGameRepositoryAdapter repositoryAdapter;

    @Test
    void shouldSaveAndRetrieveGameSuccessfully() {
        // Arrange
        String gameId = UUID.randomUUID().toString();
        Player host = new Player("p1", "Alice");
        WinningCondition condition = WinningCondition.bestOf(3);
        Game game = new Game(gameId, host, new ClassicRules(), condition);

        // Act
        repositoryAdapter.save(game);
        Optional<Game> retrievedGame = repositoryAdapter.findById(gameId);

        // Assert
        assertTrue(retrievedGame.isPresent());
        assertEquals(gameId, retrievedGame.get().getId());
        assertEquals("p1", retrievedGame.get().getPlayer1().id());
        assertEquals("WAITING_FOR_PLAYER", retrievedGame.get().getStatus().name());
    }

    @Test
    void shouldSaveGameWithRoundsAndRetrieveCorrectly() {
        String gameId = UUID.randomUUID().toString();
        Player host = new Player("p1", "Alice");
        Player guest = new Player("p2", "Bob");
        Game game = new Game(gameId, host, guest, new ClassicRules(), WinningCondition.bestOf(3));

        // Simulamos una ronda completada
        game.playRound(host, Move.ROCK);
        game.playRound(guest, Move.SCISSORS);

        repositoryAdapter.save(game);
        Optional<Game> retrievedGame = repositoryAdapter.findById(gameId);

        assertTrue(retrievedGame.isPresent());
        assertEquals(1, retrievedGame.get().getScorePlayer1());
        assertEquals(0, retrievedGame.get().getScorePlayer2());
        assertEquals(1, retrievedGame.get().getRoundsHistory().size());
    }

    @Test
    void shouldReturnEmptyWhenGameDoesNotExist() {
        Optional<Game> retrievedGame = repositoryAdapter.findById("non-existent-id");
        assertTrue(retrievedGame.isEmpty());
    }

    @Test
    void shouldDeleteGameSuccessfully() {
        String gameId = UUID.randomUUID().toString();
        Game game = new Game(gameId, new Player("p1", "Alice"), new ClassicRules(), WinningCondition.bestOf(1));
        repositoryAdapter.save(game);

        repositoryAdapter.delete(gameId);
        Optional<Game> retrievedGame = repositoryAdapter.findById(gameId);

        assertTrue(retrievedGame.isEmpty());
    }
}