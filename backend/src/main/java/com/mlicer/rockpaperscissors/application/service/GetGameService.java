package com.mlicer.rockpaperscissors.application.service;

import com.mlicer.rockpaperscissors.application.exception.GameNotFoundException;
import com.mlicer.rockpaperscissors.application.port.in.GetGameUseCase;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.model.Game;

public class GetGameService implements GetGameUseCase {

    private final GameRepository gameRepository;

    public GetGameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Game getGame(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }
}