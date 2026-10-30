package com.mlicer.rockpaperscissors.application.dto;

public record PlayTurnCommand(
        String gameId,
        String playerId,
        String move
) {
    public PlayTurnCommand {
        if (gameId == null || gameId.isBlank()) {
            throw new IllegalArgumentException("Game ID is required");
        }
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player ID is required");
        }
        if (move == null || move.isBlank()) {
            throw new IllegalArgumentException("Move is required");
        }
    }
}