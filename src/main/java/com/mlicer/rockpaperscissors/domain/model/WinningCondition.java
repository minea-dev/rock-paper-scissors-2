package com.mlicer.rockpaperscissors.domain.model;

public record WinningCondition(int totalRounds, int targetScore) {

    public WinningCondition {
        if (totalRounds <= 0) {
            throw new IllegalArgumentException("Total rounds must be greater than zero.");
        }
        if (targetScore <= 0 || targetScore > totalRounds) {
            throw new IllegalArgumentException("Target score is invalid for the given rounds.");
        }
    }

    public static WinningCondition bestOf(int rounds) {
        if (rounds % 2 == 0 || rounds < 1) {
            throw new IllegalArgumentException("Best of N rounds must be an odd number greater than zero.");
        }
        int target = (rounds / 2) + 1;
        return new WinningCondition(rounds, target);
    }
}