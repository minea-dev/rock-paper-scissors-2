package com.mlicer.rockpaperscissors.application.dto;

import com.mlicer.rockpaperscissors.domain.model.Game;

import java.util.List;

public record GameResponseDto(
        String id,
        String status,
        PlayerDto player1,
        PlayerDto player2,
        int scorePlayer1,
        int scorePlayer2,
        boolean hasUserMoved,
        boolean hasOpponentMoved,
        List<RoundResponseDto> history,
        String winnerId
) {
    public static GameResponseDto from(Game game, String viewerPlayerId) {
        List<RoundResponseDto> history = game.getRoundsHistory().stream()
                .map(RoundResponseDto::from)
                .toList();

        String winnerId = game.getWinner()
                .map(player -> player.id())
                .orElse(null);

        boolean hasUserMoved = false;
        boolean hasOpponentMoved = false;

        if (viewerPlayerId != null) {
            if (viewerPlayerId.equals(game.getPlayer1().id())) {
                hasUserMoved = game.hasPlayerMoved(game.getPlayer1());
                hasOpponentMoved = game.getPlayer2() != null && game.hasPlayerMoved(game.getPlayer2());
            } else if (game.getPlayer2() != null && viewerPlayerId.equals(game.getPlayer2().id())) {
                hasUserMoved = game.hasPlayerMoved(game.getPlayer2());
                hasOpponentMoved = game.hasPlayerMoved(game.getPlayer1());
            }
        }

        return new GameResponseDto(
                game.getId(),
                game.getStatus().name(),
                PlayerDto.from(game.getPlayer1()),
                PlayerDto.from(game.getPlayer2()),
                game.getScorePlayer1(),
                game.getScorePlayer2(),
                hasUserMoved,
                hasOpponentMoved,
                history,
                winnerId
        );
    }

    public static GameResponseDto from(Game game) {
        return from(game, null);
    }
}