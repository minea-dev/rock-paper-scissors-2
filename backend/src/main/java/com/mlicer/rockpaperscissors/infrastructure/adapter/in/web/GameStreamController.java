package com.mlicer.rockpaperscissors.infrastructure.adapter.in.web;

import com.mlicer.rockpaperscissors.infrastructure.adapter.out.event.SseGameEventPublisherAdapter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/games")
public class GameStreamController {

    private final SseGameEventPublisherAdapter sseAdapter;

    public GameStreamController(SseGameEventPublisherAdapter sseAdapter) {
        this.sseAdapter = sseAdapter;
    }

    private String resolveUserId(OAuth2User principal, String guestId, String legacyUserId) {
        if (principal != null) {
            String email = principal.getAttribute("email");
            if (email != null && !email.isBlank()) {
                return email;
            }
            String login = principal.getAttribute("login");
            if (login != null && !login.isBlank()) {
                return login;
            }
            return principal.getName();
        }

        String fallback = (guestId != null && !guestId.isBlank()) ? guestId : legacyUserId;
        if (fallback != null && !fallback.isBlank()) {
            return fallback.trim();
        }

        return "guest-id";
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGameEvents(
            @PathVariable("id") String gameId,
            @AuthenticationPrincipal OAuth2User principal,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String legacyUserId) {

        String userId = resolveUserId(principal, guestId, legacyUserId);
        return sseAdapter.subscribeToRoom(gameId, userId);
    }
}