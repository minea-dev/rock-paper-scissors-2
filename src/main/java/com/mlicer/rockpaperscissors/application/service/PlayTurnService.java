package com.mlicer.rockpaperscissors.application.service;

import com.mlicer.rockpaperscissors.application.dto.PlayTurnCommand;
import com.mlicer.rockpaperscissors.application.port.in.PlayTurnUseCase;
import com.mlicer.rockpaperscissors.application.port.out.GameEventPublisher;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.GameStatus;
import com.mlicer.rockpaperscissors.domain.model.Move;
import com.mlicer.rockpaperscissors.domain.model.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayTurnService implements PlayTurnUseCase {

    private final GameRepository gameRepository;
    private final GameEventPublisher gameEventPublisher;

    public PlayTurnService(GameRepository gameRepository, GameEventPublisher gameEventPublisher) {
        this.gameRepository = gameRepository;
        this.gameEventPublisher = gameEventPublisher;
    }

    @Override
    public Game playTurn(PlayTurnCommand command) {
        Game game = gameRepository.findById(command.gameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + command.gameId()));

        Player player = resolvePlayer(game, command.playerId());

        Move move = parseMove(command.move());

        if (isSinglePlayerAgainstCpu(game)) {
            handleSinglePlayerTurn(game, player, move);
        } else {
            handleMultiplayerTurn(game, player, move);
        }

        gameRepository.save(game);

        return game;
    }

    private void handleSinglePlayerTurn(Game game, Player humanPlayer, Move humanMove) {
        game.playRound(humanPlayer, humanMove);

        List<Move> allowedMoves = game.getRules().allowedMoves();
        int randomIndex = ThreadLocalRandom.current().nextInt(allowedMoves.size());
        Move botMove = allowedMoves.get(randomIndex);

        game.playRound(game.getPlayer2(), botMove);
    }

    private void handleMultiplayerTurn(Game game, Player player, Move move) {
        int roundsBefore = game.getRoundsHistory().size();

        game.playRound(player, move);

        boolean roundCompleted = game.getRoundsHistory().size() > roundsBefore;

        if (roundCompleted) {
            gameEventPublisher.publishRoundFinished(game);

            if (game.getStatus() == GameStatus.FINISHED) {
                gameEventPublisher.publishGameOver(game);
            }
        }
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

    private Move parseMove(String rawMove) {
        try {
            return Move.valueOf(rawMove.toUpperCase().trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid move value: " + rawMove);
        }
    }

    private boolean isSinglePlayerAgainstCpu(Game game) {
        return game.getPlayer2() != null && Player.createBot().id().equals(game.getPlayer2().id());
    }
}