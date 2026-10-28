package com.mlicer.rockpaperscissors.domain.rules;

import com.mlicer.rockpaperscissors.domain.model.Move;
import com.mlicer.rockpaperscissors.domain.model.Result;

import java.util.List;

public class ClassicRules implements GameRules{
    @Override
    public List<Move> allowedMoves() {
        return List.of(Move.ROCK, Move.PAPER, Move.SCISSORS);
    }

    @Override
    public Result evaluate(Move m1, Move m2) {
        if (m1 == m2) return Result.DRAW;

        return switch (m1) {
            case ROCK -> (m2 == Move.SCISSORS) ? Result.WIN : Result.LOSE;
            case PAPER -> (m2 == Move.ROCK) ? Result.WIN : Result.LOSE;
            case SCISSORS -> (m2 == Move.PAPER) ? Result.WIN : Result.LOSE;
            default -> throw new IllegalArgumentException("Move not supported in Classic mode");
        };
    }
}
