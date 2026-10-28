package com.mlicer.rockpaperscissors.domain.rules;

import com.mlicer.rockpaperscissors.domain.model.Move;
import com.mlicer.rockpaperscissors.domain.model.Result;

import java.util.List;

public class SpockRules implements GameRules{
    @Override
    public List<Move> allowedMoves() {
        return List.of(Move.ROCK, Move.PAPER, Move.SCISSORS, Move.LIZARD, Move.SPOCK);
    }

    @Override
    public Result evaluate(Move m1, Move m2) {
        if (m1 == m2) return Result.DRAW;

        return switch (m1) {
            case ROCK -> (m2 == Move.SCISSORS || m2 == Move.LIZARD) ? Result.WIN : Result.LOSE;
            case PAPER -> (m2 == Move.ROCK || m2 == Move.SPOCK) ? Result.WIN : Result.LOSE;
            case SCISSORS -> (m2 == Move.PAPER || m2 == Move.LIZARD) ? Result.WIN : Result.LOSE;
            case LIZARD -> (m2 == Move.SPOCK || m2 == Move.PAPER) ? Result.WIN : Result.LOSE;
            case SPOCK -> (m2 == Move.SCISSORS || m2 == Move.ROCK) ? Result.WIN : Result.LOSE;
        };
    }
}
