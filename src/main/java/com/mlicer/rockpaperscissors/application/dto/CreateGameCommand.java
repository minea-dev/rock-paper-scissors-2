package com.mlicer.rockpaperscissors.application.dto;

public record CreateGameCommand(
        String hostId,
        String hostUsername,
        String gameMode,
        int bestOfRounds,
        boolean vsCPU
) {
    public CreateGameCommand {
        if (hostId == null || hostId.isBlank()) {
            throw new IllegalArgumentException("Host ID is required");
        }
        if (hostUsername == null || hostUsername.isBlank()) {
            throw new IllegalArgumentException("Host username is required");
        }
        if (bestOfRounds < 1 || bestOfRounds % 2 == 0) {
            throw new IllegalArgumentException("Rounds must be an odd number greater than zero");
        }
    }
}
