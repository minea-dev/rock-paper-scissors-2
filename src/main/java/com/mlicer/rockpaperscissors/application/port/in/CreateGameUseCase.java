package com.mlicer.rockpaperscissors.application.port.in;

import com.mlicer.rockpaperscissors.application.dto.CreateGameCommand;
import com.mlicer.rockpaperscissors.domain.model.Game;

public interface CreateGameUseCase {
    Game createGame(CreateGameCommand command);
}
