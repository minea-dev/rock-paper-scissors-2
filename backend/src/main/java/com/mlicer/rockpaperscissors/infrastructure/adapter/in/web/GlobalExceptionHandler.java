package com.mlicer.rockpaperscissors.infrastructure.adapter.in.web;

import com.mlicer.rockpaperscissors.application.exception.GameNotFoundException;
import com.mlicer.rockpaperscissors.domain.exception.GameRuleViolationException;
import com.mlicer.rockpaperscissors.domain.exception.InvalidMoveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public ProblemDetail handleGameNotFound(GameNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Game Not Found");
        problem.setType(URI.create("/errors/game-not-found"));
        return problem;
    }

    @ExceptionHandler(GameRuleViolationException.class)
    public ProblemDetail handleGameRuleViolation(GameRuleViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Game Rule Violation");
        problem.setType(URI.create("/errors/game-rule-violation"));
        return problem;
    }

    @ExceptionHandler({InvalidMoveException.class, IllegalArgumentException.class})
    public ProblemDetail handleBadRequest(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Bad Request");
        problem.setType(URI.create("/errors/bad-request"));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("/errors/internal-server-error"));
        return problem;
    }
}