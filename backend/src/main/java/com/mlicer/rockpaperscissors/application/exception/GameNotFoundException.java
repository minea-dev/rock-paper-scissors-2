package com.mlicer.rockpaperscissors.application.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String gameI) {
        super("Game not found with ID: " + gameI);
    }
}
