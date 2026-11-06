package com.mlicer.rockpaperscissors.domain.exception;

public class GameRuleViolationException extends RuntimeException {
    public GameRuleViolationException(String message) {
        super(message);
    }
}
