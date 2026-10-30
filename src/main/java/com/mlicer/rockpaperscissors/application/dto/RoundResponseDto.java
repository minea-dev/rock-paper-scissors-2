package com.mlicer.rockpaperscissors.application.dto;

import com.mlicer.rockpaperscissors.domain.model.Round;

public record RoundResponseDto(
        int roundNumber,
        String player1Move,
        String player2Move,
        String result
) {
    public static RoundResponseDto from(Round round) {
        return new RoundResponseDto(
                round.roundNumber(),
                round.player1Move().name(),
                round.player2Move().name(),
                round.result().name()
        );
    }
}