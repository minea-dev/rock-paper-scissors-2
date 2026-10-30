package com.mlicer.rockpaperscissors.application.service;

import com.mlicer.rockpaperscissors.application.dto.JoinGameCommand;
import com.mlicer.rockpaperscissors.application.port.in.JoinGameUseCase;
import com.mlicer.rockpaperscissors.application.port.out.GameEventPublisher;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.Player;

public class JoinGameService implements JoinGameUseCase {

    private final GameRepository gameRepository;
    private final GameEventPublisher gameEventPublisher;

    public JoinGameService(GameRepository gameRepository, GameEventPublisher gameEventPublisher) {
        this.gameRepository = gameRepository;
        this.gameEventPublisher = gameEventPublisher;
    }

    @Override
    public Game joinGame(JoinGameCommand command) {
        Game game = gameRepository.findById(command.gameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + command.gameId()));

        Player guest = new Player(command.guestId(), command.guestUsername());

        game.join(guest);

        gameRepository.save(game);

        gameEventPublisher.publishPlayerJoined(game);

        return game;
    }
}