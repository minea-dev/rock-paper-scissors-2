package com.mlicer.rockpaperscissors.application.port.out;

import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.Player;

public interface GameEventPublisher {

    void publishPlayerJoined(Game game);

    void publishRoundFinished(Game game);

    void publishGameOver(Game game);

    void publishPlayerLeft(Game game, Player playerWhoLeft);
}
