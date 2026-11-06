package com.mlicer.rockpaperscissors.infrastructure.adapter.out.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlicer.rockpaperscissors.application.dto.GameResponseDto;
import com.mlicer.rockpaperscissors.application.port.out.GameEventPublisher;
import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.domain.model.Player;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseGameEventPublisherAdapter implements GameEventPublisher {

    private final Map<String, List<SseClientConnection>> roomEmitters = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    public SseGameEventPublisherAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SseEmitter subscribeToRoom(String gameId, String playerId) {
        SseEmitter emitter = new SseEmitter(0L);

        SseClientConnection connection = new SseClientConnection(playerId, emitter);

        roomEmitters.computeIfAbsent(gameId, k -> new CopyOnWriteArrayList<>())
                .add(connection);

        Runnable cleanup = () -> removeEmitter(gameId, connection);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        try {
            emitter.send(SseEmitter.event().name("CONNECTION_ESTABLISHED").data("Connected to game stream"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    private void removeEmitter(String gameId, SseClientConnection connection) {
        List<SseClientConnection> room = roomEmitters.get(gameId);
        if (room != null) {
            room.remove(connection);
            if (room.isEmpty()) {
                roomEmitters.remove(gameId);
            }
        }
    }


    @Override
    public void publishPlayerJoined(Game game) {
        broadcastEvent(game, "PLAYER_JOINED");
    }

    @Override
    public void publishRoundFinished(Game game) {
        broadcastEvent(game, "ROUND_FINISHED");
    }

    @Override
    public void publishGameOver(Game game) {
        broadcastEvent(game, "GAME_OVER");
    }

    @Override
    public void publishPlayerLeft(Game game, Player playerWhoLeft) {
        broadcastEvent(game, "PLAYER_LEFT");
    }


    private void broadcastEvent(Game game, String eventName) {
        List<SseClientConnection> connections = roomEmitters.get(game.getId());
        if (connections == null || connections.isEmpty()) {
            return;
        }

        List<SseClientConnection> deadConnections = new java.util.ArrayList<>();

        for (SseClientConnection connection : connections) {
            try {
                GameResponseDto safePayload = GameResponseDto.from(game, connection.playerId());

                String jsonString = objectMapper.writeValueAsString(safePayload);

                connection.emitter().send(SseEmitter.event()
                        .name(eventName)
                        .data(jsonString));

            } catch (Exception e) {
                connection.emitter().completeWithError(e);
                deadConnections.add(connection);
            }
        }

        connections.removeAll(deadConnections);
    }

    private record SseClientConnection(String playerId, SseEmitter emitter) {}
}