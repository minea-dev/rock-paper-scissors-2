package com.mlicer.rockpaperscissors.infrastructure.adapter.out.event;

import com.mlicer.rockpaperscissors.application.port.out.GameEventPublisher;
import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.Player;
import org.springframework.stereotype.Component;

@Component
public class LogGameEventPublisherAdapter implements GameEventPublisher {

    @Override
    public void publishPlayerJoined(Game game) {
        System.out.println("EVENT: Player joined room " + game.getId());
    }

    @Override
    public void publishRoundFinished(Game game) {
        System.out.println("EVENT: Round finished in room " + game.getId());
    }

    @Override
    public void publishGameOver(Game game) {
        System.out.println("EVENT: Game over in room " + game.getId());
    }

    @Override
    public void publishPlayerLeft(Game game, Player playerWhoLeft) {
        System.out.println("EVENT: Player " + playerWhoLeft.username() + " abandoned room " + game.getId());
    }
}