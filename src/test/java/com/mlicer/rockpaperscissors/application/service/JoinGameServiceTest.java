package com.mlicer.rockpaperscissors.application.service;

import com.mlicer.rockpaperscissors.application.dto.JoinGameCommand;
import com.mlicer.rockpaperscissors.application.port.out.GameEventPublisher;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.GameStatus;
import com.mlicer.rockpaperscissors.domain.model.Player;
import com.mlicer.rockpaperscissors.domain.model.WinningCondition;
import com.mlicer.rockpaperscissors.domain.rules.ClassicRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinGameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameEventPublisher gameEventPublisher;

    private JoinGameService joinGameService;
    private Game waitingGame;
    private Player host;

    @BeforeEach
    void setUp() {
        joinGameService = new JoinGameService(gameRepository, gameEventPublisher);
        host = new Player("host-1", "Alice");

        waitingGame = new Game("room-123", host, new ClassicRules(), WinningCondition.bestOf(3));
    }

    @Test
    void shouldAllowGuestToJoinAndOrchestrateCorrectly() {
        JoinGameCommand command = new JoinGameCommand("room-123", "guest-2", "Bob");
        when(gameRepository.findById("room-123")).thenReturn(Optional.of(waitingGame));

        Game updatedGame = joinGameService.joinGame(command);

        assertEquals(GameStatus.IN_PROGRESS, updatedGame.getStatus());
        assertEquals("guest-2", updatedGame.getPlayer2().id());

        verify(gameRepository, times(1)).save(updatedGame);
        verify(gameEventPublisher, times(1)).publishPlayerJoined(updatedGame);
    }

    @Test
    void shouldThrowExceptionWhenGameDoesNotExist() {
        JoinGameCommand command = new JoinGameCommand("invalid-room", "guest-2", "Bob");
        when(gameRepository.findById("invalid-room")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> joinGameService.joinGame(command)
        );
        assertEquals("Game not found with ID: invalid-room", exception.getMessage());

        verify(gameRepository, never()).save(any());
        verify(gameEventPublisher, never()).publishPlayerJoined(any());
    }

    @Test
    void shouldPropagateDomainExceptionWhenGameIsAlreadyFull() {
        waitingGame.join(new Player("another-guest", "Charlie"));

        JoinGameCommand command = new JoinGameCommand("room-123", "guest-2", "Bob");
        when(gameRepository.findById("room-123")).thenReturn(Optional.of(waitingGame));

        assertThrows(IllegalStateException.class, () -> joinGameService.joinGame(command));

        verify(gameRepository, never()).save(any());
        verify(gameEventPublisher, never()).publishPlayerJoined(any());
    }
}