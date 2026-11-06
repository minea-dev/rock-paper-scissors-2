package com.mlicer.rockpaperscissors.application.port.in;

import com.mlicer.rockpaperscissors.application.dto.PlayTurnCommand;
import com.mlicer.rockpaperscissors.domain.model.Game;

public interface PlayTurnUseCase {

    Game playTurn(PlayTurnCommand command);

}