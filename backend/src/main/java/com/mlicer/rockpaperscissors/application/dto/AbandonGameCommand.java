package com.mlicer.rockpaperscissors.application.dto;

public record AbandonGameCommand(
        String gameId,
        String playerId
) {
    public AbandonGameCommand {
        if (gameId == null || gameId.isBlank()) {
            throw new IllegalArgumentException("Game ID is required");
        }
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player ID is required");
        }
    }
}
