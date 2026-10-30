package com.mlicer.rockpaperscissors.application.port.in;

import com.mlicer.rockpaperscissors.domain.model.Game;

public interface GetGameUseCase {

    Game getGame(String gameId);
}