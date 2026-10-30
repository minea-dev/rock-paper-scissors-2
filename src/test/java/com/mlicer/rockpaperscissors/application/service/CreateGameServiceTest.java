package com.mlicer.rockpaperscissors.application.service;

import com.mlicer.rockpaperscissors.application.dto.CreateGameCommand;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGameServiceTest {

    @Mock
    private GameRepository gameRepository;

    private CreateGameService createGameService;

    @BeforeEach
    void setUp() {
        createGameService = new CreateGameService(gameRepository);
    }

    @Test
    void shouldCreateAndPersistSinglePlayerGameAgainstCpu() {
        CreateGameCommand command = new CreateGameCommand("user-1", "Alice", "CLASSIC", 3, true);

        Game game = createGameService.createGame(command);

        assertNotNull(game.getId());
        assertEquals("user-1", game.getPlayer1().id());
        assertNotNull(game.getPlayer2());
        assertEquals("CPU", game.getPlayer2().id());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());

        verify(gameRepository, times(1)).save(game);
    }

    @Test
    void shouldCreateAndPersistMultiplayerGameWaitingForGuest() {
        CreateGameCommand command = new CreateGameCommand("user-1", "Alice", "EXTENDED", 5, false);

        Game game = createGameService.createGame(command);

        assertNotNull(game.getId());
        assertEquals("user-1", game.getPlayer1().id());
        assertNull(game.getPlayer2());
        assertEquals(GameStatus.WAITING_FOR_PLAYER, game.getStatus());

        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository).save(gameCaptor.capture());
        assertEquals(game.getId(), gameCaptor.getValue().getId());
    }

    @Test
    void shouldThrowExceptionWhenGameModeIsInvalid() {
        CreateGameCommand command = new CreateGameCommand("user-1", "Alice", "UNKNOWN_MODE", 3, false);

        assertThrows(IllegalArgumentException.class, () -> createGameService.createGame(command));
        verifyNoInteractions(gameRepository);
    }
}