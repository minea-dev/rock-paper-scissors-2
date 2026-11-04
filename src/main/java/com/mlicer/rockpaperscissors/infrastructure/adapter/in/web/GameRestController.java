package com.mlicer.rockpaperscissors.infrastructure.adapter.in.web;

import com.mlicer.rockpaperscissors.application.dto.AbandonGameCommand;
import com.mlicer.rockpaperscissors.application.dto.CreateGameCommand;
import com.mlicer.rockpaperscissors.application.dto.GameResponseDto;
import com.mlicer.rockpaperscissors.application.dto.JoinGameCommand;
import com.mlicer.rockpaperscissors.application.dto.PlayTurnCommand;
import com.mlicer.rockpaperscissors.application.port.in.AbandonGameUseCase;
import com.mlicer.rockpaperscissors.application.port.in.CreateGameUseCase;
import com.mlicer.rockpaperscissors.application.port.in.GetGameUseCase;
import com.mlicer.rockpaperscissors.application.port.in.JoinGameUseCase;
import com.mlicer.rockpaperscissors.application.port.in.PlayTurnUseCase;
import com.mlicer.rockpaperscissors.domain.model.Game;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<GameResponseDto> createGame(
            @RequestBody CreateGameCommand command,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String userId) {

        Game game = createGameUseCase.createGame(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GameResponseDto.from(game, userId));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<GameResponseDto> joinGame(
            @PathVariable String id,
            @RequestBody JoinGameCommand partialCommand,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String userId) {

        JoinGameCommand command = new JoinGameCommand(id, partialCommand.guestId(), partialCommand.guestUsername());
        Game game = joinGameUseCase.joinGame(command);
        return ResponseEntity.ok(GameResponseDto.from(game, userId));
    }

    @PostMapping("/{id}/turns")
    public ResponseEntity<GameResponseDto> playTurn(
            @PathVariable String id,
            @RequestBody PlayTurnCommand partialCommand,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String userId) {

        PlayTurnCommand command = new PlayTurnCommand(id, partialCommand.playerId(), partialCommand.move());
        Game game = playTurnUseCase.playTurn(command);
        return ResponseEntity.ok(GameResponseDto.from(game, userId));
    }

    @PostMapping("/{id}/abandon")
    public ResponseEntity<GameResponseDto> abandonGame(
            @PathVariable String id,
            @RequestBody AbandonGameCommand partialCommand,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String userId) {

        AbandonGameCommand command = new AbandonGameCommand(id, partialCommand.playerId());
        Game game = abandonGameUseCase.abandonGame(command);
        return ResponseEntity.ok(GameResponseDto.from(game, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDto> getGame(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-id") String userId) {

        Game game = getGameUseCase.getGame(id);
        return ResponseEntity.ok(GameResponseDto.from(game, userId));
    }
}