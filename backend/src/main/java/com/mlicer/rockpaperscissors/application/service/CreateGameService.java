package com.mlicer.rockpaperscissors.application.service;

import com.mlicer.rockpaperscissors.application.dto.CreateGameCommand;
import com.mlicer.rockpaperscissors.application.port.in.CreateGameUseCase;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.Player;
import com.mlicer.rockpaperscissors.domain.model.WinningCondition;
import com.mlicer.rockpaperscissors.domain.rules.ClassicRules;
import com.mlicer.rockpaperscissors.domain.rules.GameRules;
import com.mlicer.rockpaperscissors.domain.rules.SpockRules;

import java.util.UUID;

public class CreateGameService implements CreateGameUseCase {

    private final GameRepository gameRepository;

    public CreateGameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Game createGame(CreateGameCommand command) {
        String gameId = UUID.randomUUID().toString();

        GameRules rules = resolveRules(command.gameMode());

        WinningCondition winningCondition = WinningCondition.bestOf(command.bestOfRounds());

        Player host = new Player(command.hostId(), command.hostUsername());

        Game game;
        if (command.vsCPU()) {
            Player bot = Player.createBot();
            game = new Game(gameId, host, bot, rules, winningCondition);
        } else {
            game = new Game(gameId, host, rules, winningCondition);
        }

        gameRepository.save(game);

        return game;
    }

    private GameRules resolveRules(String mode) {
        if ("CLASSIC".equalsIgnoreCase(mode)) {
            return new ClassicRules();
        } else if ("EXTENDED".equalsIgnoreCase(mode) || "SPOCK".equalsIgnoreCase(mode)) {
            return new SpockRules();
        }
        throw new IllegalArgumentException("Unsupported game mode: " + mode);
    }
}