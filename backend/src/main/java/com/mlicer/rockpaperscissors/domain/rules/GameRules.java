package com.mlicer.rockpaperscissors.domain.rules;

import com.mlicer.rockpaperscissors.domain.model.Move;
import com.mlicer.rockpaperscissors.domain.model.Result;

import java.util.List;

public interface GameRules {
    List<Move> allowedMoves();
    Result evaluate(Move player1Move, Move player2Move);
}
