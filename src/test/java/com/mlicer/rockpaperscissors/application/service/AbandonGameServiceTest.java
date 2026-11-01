package com.mlicer.rockpaperscissors.application.service;

import com.mlicer.rockpaperscissors.application.dto.AbandonGameCommand;
import com.mlicer.rockpaperscissors.application.exception.GameNotFoundException;
import com.mlicer.rockpaperscissors.application.port.out.GameEventPublisher;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.exception.GameRuleViolationException;
import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.GameStatus;
import com.mlicer.rockpaperscissors.domain.model.Move;
import com.mlicer.rockpaperscissors.domain.model.Player;
import com.mlicer.rockpaperscissors.domain.model.WinningCondition;
import com.mlicer.rockpaperscissors.domain.rules.ClassicRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbandonGameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameEventPublisher gameEventPublisher;

    private AbandonGameService abandonGameService;

    private Player host;
    private Player guest;
    private Game game;

    @BeforeEach
    void setUp() {
        abandonGameService = new AbandonGameService(gameRepository, gameEventPublisher);
        host = new Player("user-1", "Alice");
        guest = new Player("user-2", "Bob");
        game = new Game("game-100", host, guest, new ClassicRules(), WinningCondition.bestOf(3));
    }

    @Test
    void shouldAbandonGamePersistAndPublishEventsWhenPlayerSurrenders() {
        when(gameRepository.findById("game-100")).thenReturn(Optional.of(game));
        AbandonGameCommand command = new AbandonGameCommand("game-100", host.id());

        Game updatedGame = abandonGameService.abandonGame(command);

        assertEquals(GameStatus.ABANDONED, updatedGame.getStatus());
        assertEquals(Optional.of(guest), updatedGame.getWinner());

        InOrder inOrder = inOrder(gameRepository, gameEventPublisher);
        inOrder.verify(gameRepository).save(updatedGame);
        inOrder.verify(gameEventPublisher).publishPlayerLeft(updatedGame, host);
        inOrder.verify(gameEventPublisher).publishGameOver(updatedGame);
    }

    @Test
    void shouldThrowExceptionWhenGameNotFound() {
        when(gameRepository.findById("non-existent")).thenReturn(Optional.empty());
        AbandonGameCommand command = new AbandonGameCommand("non-existent", host.id());

        assertThrows(GameNotFoundException.class, () -> abandonGameService.abandonGame(command));

        verify(gameRepository, never()).save(any());
        verifyNoInteractions(gameEventPublisher);
    }

    @Test
    void shouldThrowExceptionWhenPlayerDoesNotBelongToGame() {
        when(gameRepository.findById("game-100")).thenReturn(Optional.of(game));
        AbandonGameCommand command = new AbandonGameCommand("game-100", "external-user");

        assertThrows(IllegalArgumentException.class, () -> abandonGameService.abandonGame(command));

        verify(gameRepository, never()).save(any());
        verifyNoInteractions(gameEventPublisher);
    }

    @Test
    void shouldPropagateDomainExceptionAndNotPersistWhenGameIsAlreadyFinished() {
        Game finishedGame = new Game("game-finished", host, guest, new ClassicRules(), WinningCondition.bestOf(1));
        finishedGame.playRound(host, Move.ROCK);
        finishedGame.playRound(guest, Move.SCISSORS);
        assertEquals(GameStatus.FINISHED, finishedGame.getStatus());

        when(gameRepository.findById("game-finished")).thenReturn(Optional.of(finishedGame));
        AbandonGameCommand command = new AbandonGameCommand("game-finished", host.id());

        assertThrows(GameRuleViolationException.class, () -> abandonGameService.abandonGame(command));

        verify(gameRepository, never()).save(any());
        verifyNoInteractions(gameEventPublisher);
    }
}