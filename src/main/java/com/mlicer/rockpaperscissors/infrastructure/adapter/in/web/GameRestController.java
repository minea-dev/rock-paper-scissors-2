package com.mlicer.rockpaperscissors.infrastructure.adapter.in.web;

import com.mlicer.rockpaperscissors.application.dto.*;
import com.mlicer.rockpaperscissors.application.port.in.AbandonGameUseCase;
import com.mlicer.rockpaperscissors.application.port.in.CreateGameUseCase;
import com.mlicer.rockpaperscissors.application.port.in.GetGameUseCase;
import com.mlicer.rockpaperscissors.application.port.in.JoinGameUseCase;
import com.mlicer.rockpaperscissors.application.port.in.PlayTurnUseCase;
import com.mlicer.rockpaperscissors.domain.model.Game;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameRestController {

    private final CreateGameUseCase createGameUseCase;
    private final JoinGameUseCase joinGameUseCase;
    private final PlayTurnUseCase playTurnUseCase;
    private final GetGameUseCase getGameUseCase;
    private final AbandonGameUseCase abandonGameUseCase;

    public GameRestController(CreateGameUseCase createGameUseCase,
                              JoinGameUseCase joinGameUseCase,
                              PlayTurnUseCase playTurnUseCase,
                              GetGameUseCase getGameUseCase,
                              AbandonGameUseCase abandonGameUseCase) {
        this.createGameUseCase = createGameUseCase;
        this.joinGameUseCase = joinGameUseCase;
        this.playTurnUseCase = playTurnUseCase;
        this.getGameUseCase = getGameUseCase;
        this.abandonGameUseCase = abandonGameUseCase;
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

    @PostMapping
    public ResponseEntity<GameResponseDto> createGame(
            @RequestBody CreateGameCommand request,
            @AuthenticationPrincipal OAuth2User principal,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String legacyUserId) {

        String resolvedUserId = resolveUserId(principal, guestId, legacyUserId);

        CreateGameCommand command = new CreateGameCommand(
                resolvedUserId,
                request.hostUsername(),
                request.gameMode(),
                request.bestOfRounds(),
                request.vsCPU()
        );

        Game game = createGameUseCase.createGame(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GameResponseDto.from(game, resolvedUserId));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<GameResponseDto> joinGame(
            @PathVariable String id,
            @RequestBody JoinGameCommand partialCommand,
            @AuthenticationPrincipal OAuth2User principal,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String legacyUserId) {

        String resolvedUserId = resolveUserId(principal, guestId, legacyUserId);

        JoinGameCommand command = new JoinGameCommand(
                id,
                resolvedUserId,
                partialCommand.guestUsername()
        );

        Game game = joinGameUseCase.joinGame(command);
        return ResponseEntity.ok(GameResponseDto.from(game, resolvedUserId));
    }

    @PostMapping("/{id}/turns")
    public ResponseEntity<GameResponseDto> playTurn(
            @PathVariable String id,
            @RequestBody PlayTurnRequest request,
            @AuthenticationPrincipal OAuth2User principal,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String legacyUserId) {

        String resolvedUserId = resolveUserId(principal, guestId, legacyUserId);

        PlayTurnCommand command = new PlayTurnCommand(id, resolvedUserId, request.move());

        Game game = playTurnUseCase.playTurn(command);
        return ResponseEntity.ok(GameResponseDto.from(game, resolvedUserId));
    }

    @PostMapping("/{id}/abandon")
    public ResponseEntity<GameResponseDto> abandonGame(
            @PathVariable String id,
            @RequestBody AbandonGameCommand partialCommand,
            @AuthenticationPrincipal OAuth2User principal,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String legacyUserId) {

        String resolvedUserId = resolveUserId(principal, guestId, legacyUserId);

        AbandonGameCommand command = new AbandonGameCommand(
                id,
                resolvedUserId
        );

        Game game = abandonGameUseCase.abandonGame(command);
        return ResponseEntity.ok(GameResponseDto.from(game, resolvedUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDto> getGame(
            @PathVariable String id,
            @AuthenticationPrincipal OAuth2User principal,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String legacyUserId) {

        String resolvedUserId = resolveUserId(principal, guestId, legacyUserId);

        Game game = getGameUseCase.getGame(id);
        return ResponseEntity.ok(GameResponseDto.from(game, resolvedUserId));
    }
}