package com.mlicer.rockpaperscissors.application.service;

import com.mlicer.rockpaperscissors.application.exception.GameNotFoundException;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.model.Game;
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
class GetGameServiceTest {

    @Mock
    private GameRepository gameRepository;

    private GetGameService getGameService;

    @BeforeEach
    void setUp() {
        getGameService = new GetGameService(gameRepository);
    }

    @Test
    void shouldReturnGameWhenExists() {
        String gameId = "game-123";
        Game game = new Game(gameId, new Player("p1", "Alice"), new ClassicRules(), WinningCondition.bestOf(3));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        Game result = getGameService.getGame(gameId);

        assertNotNull(result);
        assertEquals(gameId, result.getId());
        verify(gameRepository, times(1)).findById(gameId);
    }

    @Test
    void shouldThrowGameNotFoundExceptionWhenGameDoesNotExist() {
        String nonExistentId = "non-existent-id";
        when(gameRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> getGameService.getGame(nonExistentId));
        verify(gameRepository, times(1)).findById(nonExistentId);
    }
}