package com.mlicer.rockpaperscissors.application.service;

import com.mlicer.rockpaperscissors.application.dto.AbandonGameCommand;
import com.mlicer.rockpaperscissors.application.port.in.AbandonGameUseCase;
import com.mlicer.rockpaperscissors.application.port.out.GameEventPublisher;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.Player;

public class AbandonGameService implements AbandonGameUseCase {

    private final GameRepository gameRepository;
    private final GameEventPublisher gameEventPublisher;

    public AbandonGameService(GameRepository gameRepository, GameEventPublisher gameEventPublisher) {
        this.gameRepository = gameRepository;
        this.gameEventPublisher = gameEventPublisher;
    }

    @Override
    public Game abandonGame(AbandonGameCommand command) {
        Game game = gameRepository.findById(command.gameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + command.gameId()));

        Player playerWhoLeft = resolvePlayer(game, command.playerId());

        game.abandon(playerWhoLeft);

        gameRepository.save(game);

        gameEventPublisher.publishPlayerLeft(game, playerWhoLeft);
        gameEventPublisher.publishGameOver(game);

        return game;
    }

    private Player resolvePlayer(Game game, String playerId) {
        if (playerId.equals(game.getPlayer1().id())) {
            return game.getPlayer1();
        }
        if (game.getPlayer2() != null && playerId.equals(game.getPlayer2().id())) {
            return game.getPlayer2();
        }
        throw new IllegalArgumentException("Player does not belong to this game: " + playerId);
    }
}