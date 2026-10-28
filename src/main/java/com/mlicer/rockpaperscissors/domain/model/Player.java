package com.mlicer.rockpaperscissors.domain.model;

public record Player(String id, String username) {

    public Player {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Player ID cannot be empty or null");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty or null");
        }
    }
}