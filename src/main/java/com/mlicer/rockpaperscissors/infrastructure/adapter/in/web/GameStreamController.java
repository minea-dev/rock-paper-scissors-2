package com.mlicer.rockpaperscissors.infrastructure.adapter.in.web;

import com.mlicer.rockpaperscissors.infrastructure.adapter.out.event.SseGameEventPublisherAdapter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/games")
public class GameStreamController {

    private final SseGameEventPublisherAdapter sseAdapter;

    public GameStreamController(SseGameEventPublisherAdapter sseAdapter) {
        this.sseAdapter = sseAdapter;
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGameEvents(
            @PathVariable("id") String gameId,
            @RequestHeader(value = "X-User-Id") String userId) {

        return sseAdapter.subscribeToRoom(gameId, userId);
    }
}