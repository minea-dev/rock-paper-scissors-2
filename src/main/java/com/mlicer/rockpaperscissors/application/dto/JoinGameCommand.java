package com.mlicer.rockpaperscissors.application.dto;

public record JoinGameCommand(
        String gameId,
        String guestId,
        String guestUsername
) {
    public JoinGameCommand {
        if (gameId == null || gameId.isBlank()) {
            throw new IllegalArgumentException("Game ID is required");
        }
        if (guestId == null || guestId.isBlank()) {
            throw new IllegalArgumentException("Guest ID is required");
        }
        if (guestUsername == null || guestUsername.isBlank()) {
            throw new IllegalArgumentException("Guest username is required");
        }
    }
}
