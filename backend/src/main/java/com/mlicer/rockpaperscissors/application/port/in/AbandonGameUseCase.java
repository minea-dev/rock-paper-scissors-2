package com.mlicer.rockpaperscissors.application.port.in;

import com.mlicer.rockpaperscissors.application.dto.AbandonGameCommand;
import com.mlicer.rockpaperscissors.domain.model.Game;

public interface AbandonGameUseCase {
    Game abandonGame(AbandonGameCommand command);
}
