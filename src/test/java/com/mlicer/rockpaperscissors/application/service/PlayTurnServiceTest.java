package com.mlicer.rockpaperscissors.application.service;

import com.mlicer.rockpaperscissors.application.dto.PlayTurnCommand;
import com.mlicer.rockpaperscissors.application.port.out.GameEventPublisher;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.model.*;
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
class PlayTurnServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameEventPublisher gameEventPublisher;

    private PlayTurnService playTurnService;

    private Player host;
    private Player guest;
    private Player cpu;

    @BeforeEach
    void setUp() {
        playTurnService = new PlayTurnService(gameRepository, gameEventPublisher);
        host = new Player("user-1", "Alice");
        guest = new Player("user-2", "Bob");
        cpu = Player.createBot();
    }

    @Test
    void shouldExecuteSinglePlayerTurnSynchronouslyAgainstCpu() {
        Game cpuGame = new Game("game-cpu", host, cpu, new ClassicRules(), WinningCondition.bestOf(3));
        when(gameRepository.findById("game-cpu")).thenReturn(Optional.of(cpuGame));

        PlayTurnCommand command = new PlayTurnCommand("game-cpu", host.id(), "ROCK");

        Game resultGame = playTurnService.playTurn(command);

        assertEquals(1, resultGame.getRoundsHistory().size());
        Round playedRound = resultGame.getRoundsHistory().get(0);
        assertEquals(Move.ROCK, playedRound.player1Move());
        assertNotNull(playedRound.player2Move());

        verify(gameRepository, times(1)).save(cpuGame);
        verifyNoInteractions(gameEventPublisher);
    }

    @Test
    void shouldAcceptFirstMoveInMultiplayerWithoutResolvingRound() {
        Game multiGame = new Game("game-multi", host, guest, new ClassicRules(), WinningCondition.bestOf(3));
        when(gameRepository.findById("game-multi")).thenReturn(Optional.of(multiGame));

        PlayTurnCommand command = new PlayTurnCommand("game-multi", host.id(), "SCISSORS");

        Game resultGame = playTurnService.playTurn(command);

        assertTrue(resultGame.getRoundsHistory().isEmpty());
        assertTrue(resultGame.hasPlayerMoved(host));
        assertFalse(resultGame.hasPlayerMoved(guest));

        verify(gameRepository, times(1)).save(multiGame);
        verifyNoInteractions(gameEventPublisher);
    }

    @Test
    void shouldResolveRoundAndPublishEventWhenBothPlayersMoveInMultiplayer() {
        Game multiGame = new Game("game-multi", host, guest, new ClassicRules(), WinningCondition.bestOf(3));
        when(gameRepository.findById("game-multi")).thenReturn(Optional.of(multiGame));

        playTurnService.playTurn(new PlayTurnCommand("game-multi", host.id(), "ROCK"));

        playTurnService.playTurn(new PlayTurnCommand("game-multi", guest.id(), "PAPER"));

        assertEquals(1, multiGame.getRoundsHistory().size());
        assertEquals(1, multiGame.getScorePlayer2()); // Bob gana con PAPER

        verify(gameRepository, times(2)).save(multiGame);
        verify(gameEventPublisher, times(1)).publishRoundFinished(multiGame);
        verify(gameEventPublisher, never()).publishGameOver(any());
    }

    @Test
    void shouldPublishGameOverWhenMultiplayerRoundReachesWinningCondition() {
        Game multiGame = new Game("game-multi", host, guest, new ClassicRules(), WinningCondition.bestOf(1));
        when(gameRepository.findById("game-multi")).thenReturn(Optional.of(multiGame));

        playTurnService.playTurn(new PlayTurnCommand("game-multi", host.id(), "ROCK"));
        playTurnService.playTurn(new PlayTurnCommand("game-multi", guest.id(), "SCISSORS"));

        assertEquals(GameStatus.FINISHED, multiGame.getStatus());
        assertEquals(Optional.of(host), multiGame.getWinner());

        verify(gameEventPublisher, times(1)).publishRoundFinished(multiGame);
        verify(gameEventPublisher, times(1)).publishGameOver(multiGame);
    }

    @Test
    void shouldThrowExceptionWhenGameNotFound() {
        when(gameRepository.findById("invalid-id")).thenReturn(Optional.empty());

        PlayTurnCommand command = new PlayTurnCommand("invalid-id", host.id(), "ROCK");

        assertThrows(IllegalArgumentException.class, () -> playTurnService.playTurn(command));
        verify(gameRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPlayerDoesNotBelongToGame() {
        Game multiGame = new Game("game-multi", host, guest, new ClassicRules(), WinningCondition.bestOf(3));
        when(gameRepository.findById("game-multi")).thenReturn(Optional.of(multiGame));

        PlayTurnCommand command = new PlayTurnCommand("game-multi", "intruder-id", "ROCK");

        assertThrows(IllegalArgumentException.class, () -> playTurnService.playTurn(command));
        verify(gameRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenMoveIsInvalidString() {
        Game multiGame = new Game("game-multi", host, guest, new ClassicRules(), WinningCondition.bestOf(3));
        when(gameRepository.findById("game-multi")).thenReturn(Optional.of(multiGame));

        PlayTurnCommand command = new PlayTurnCommand("game-multi", host.id(), "INVALID_MOVE");

        assertThrows(IllegalArgumentException.class, () -> playTurnService.playTurn(command));
        verify(gameRepository, never()).save(any());
    }
}