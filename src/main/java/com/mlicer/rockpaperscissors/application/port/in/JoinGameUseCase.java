package com.mlicer.rockpaperscissors.application.port.in;

import com.mlicer.rockpaperscissors.application.dto.JoinGameCommand;
import com.mlicer.rockpaperscissors.domain.model.Game;

public interface JoinGameUseCase {
    Game joinGame(JoinGameCommand command);
}
