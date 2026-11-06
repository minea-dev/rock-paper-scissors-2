package com.mlicer.rockpaperscissors.domain.model;

public record Round(int roundNumber, Move player1Move, Move player2Move, Result result) {

    public Round {
        if (roundNumber <= 0) {
            throw new IllegalArgumentException("Round number must be greater than zero.");
        }
        if (player1Move == null || player2Move == null) {
            throw new IllegalArgumentException("Player moves cannot be null.");
        }
        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null.");
        }
    }
}