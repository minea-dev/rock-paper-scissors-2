package com.mlicer.rockpaperscissors.infrastructure.adapter.in.web;

import com.mlicer.rockpaperscissors.application.dto.CreateGameCommand;
import com.mlicer.rockpaperscissors.application.dto.GameResponseDto;
import com.mlicer.rockpaperscissors.application.dto.JoinGameCommand;
import com.mlicer.rockpaperscissors.application.dto.PlayTurnCommand;
import com.mlicer.rockpaperscissors.domain.model.GameStatus;
import com.mlicer.rockpaperscissors.infrastructure.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

class GameFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("You must execute the complete E2E lifecycle: creation, joining, move secrecy, resolution, and victory")
    void shouldExecuteFullMultiplayerGameLifecycleSuccessfully() {
        String aliceId = "alice-e2e";
        String bobId = "bob-e2e";

        CreateGameCommand createCommand = new CreateGameCommand(
                aliceId, "Alice", "CLASSIC", 1, false
        );

        HttpEntity<CreateGameCommand> createEntity = new HttpEntity<>(createCommand, createHeaders(aliceId));
        ResponseEntity<GameResponseDto> createResponse = restTemplate.exchange(
                "/api/games", HttpMethod.POST, createEntity, GameResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        GameResponseDto createdGame = createResponse.getBody();
        assertNotNull(createdGame);

        String gameId = createdGame.id();
        assertNotNull(gameId);
        assertEquals(GameStatus.WAITING_FOR_PLAYER.name(), createdGame.status());
        assertEquals(aliceId, createdGame.player1().id());
        assertNull(createdGame.player2());

        JoinGameCommand joinCommand = new JoinGameCommand(gameId, bobId, "Bob");

        HttpEntity<JoinGameCommand> joinEntity = new HttpEntity<>(joinCommand, createHeaders(bobId));
        ResponseEntity<GameResponseDto> joinResponse = restTemplate.exchange(
                "/api/games/" + gameId + "/join", HttpMethod.POST, joinEntity, GameResponseDto.class
        );

        assertEquals(HttpStatus.OK, joinResponse.getStatusCode());
        GameResponseDto joinedGame = joinResponse.getBody();
        assertNotNull(joinedGame);
        assertEquals(GameStatus.IN_PROGRESS.name(), joinedGame.status());
        assertNotNull(joinedGame.player2());
        assertEquals(bobId, joinedGame.player2().id());

        PlayTurnCommand aliceTurnCommand = new PlayTurnCommand(gameId, aliceId, "ROCK");

        HttpEntity<PlayTurnCommand> aliceTurnEntity = new HttpEntity<>(aliceTurnCommand, createHeaders(aliceId));
        ResponseEntity<GameResponseDto> aliceTurnResponse = restTemplate.exchange(
                "/api/games/" + gameId + "/turns", HttpMethod.POST, aliceTurnEntity, GameResponseDto.class
        );

        assertEquals(HttpStatus.OK, aliceTurnResponse.getStatusCode());
        GameResponseDto gameAfterAliceMove = aliceTurnResponse.getBody();
        assertNotNull(gameAfterAliceMove);
        assertTrue(gameAfterAliceMove.hasUserMoved());
        assertFalse(gameAfterAliceMove.hasOpponentMoved());

        ResponseEntity<GameResponseDto> bobStateResponse = restTemplate.exchange(
                "/api/games/" + gameId, HttpMethod.GET, new HttpEntity<>(createHeaders(bobId)), GameResponseDto.class
        );
        GameResponseDto gameViewedByBob = bobStateResponse.getBody();
        assertNotNull(gameViewedByBob);

        assertTrue(gameViewedByBob.hasOpponentMoved());
        assertFalse(gameViewedByBob.hasUserMoved());
        assertTrue(gameViewedByBob.history().isEmpty());

        PlayTurnCommand bobTurnCommand = new PlayTurnCommand(gameId, bobId, "SCISSORS");

        HttpEntity<PlayTurnCommand> bobTurnEntity = new HttpEntity<>(bobTurnCommand, createHeaders(bobId));
        ResponseEntity<GameResponseDto> bobTurnResponse = restTemplate.exchange(
                "/api/games/" + gameId + "/turns", HttpMethod.POST, bobTurnEntity, GameResponseDto.class
        );

        assertEquals(HttpStatus.OK, bobTurnResponse.getStatusCode());
        GameResponseDto finalGame = bobTurnResponse.getBody();
        assertNotNull(finalGame);

        assertEquals(GameStatus.FINISHED.name(), finalGame.status());
        assertEquals(1, finalGame.scorePlayer1());
        assertEquals(0, finalGame.scorePlayer2());
        assertEquals(aliceId, finalGame.winnerId());

        assertEquals(1, finalGame.history().size());
        assertEquals("ROCK", finalGame.history().get(0).player1Move());
        assertEquals("SCISSORS", finalGame.history().get(0).player2Move());
    }

    private HttpHeaders createHeaders(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User-Id", userId);
        return headers;
    }
}