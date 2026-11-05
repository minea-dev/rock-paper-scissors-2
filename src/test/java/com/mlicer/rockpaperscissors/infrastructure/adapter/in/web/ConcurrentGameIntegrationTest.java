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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentGameIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should process 50 concurrent requests from the same player without corrupting round state")
    void shouldHandleConcurrentMovesGracefully() throws InterruptedException {
        String aliceId = "alice-concurrent";
        String bobId = "bob-concurrent";

        CreateGameCommand createCmd = new CreateGameCommand(
                aliceId, "Alice", "CLASSIC", 3, false
        );
        ResponseEntity<GameResponseDto> createRes = restTemplate.exchange(
                "/api/games", HttpMethod.POST, new HttpEntity<>(createCmd, createHeaders(aliceId)), GameResponseDto.class
        );
        String gameId = createRes.getBody().id();

        JoinGameCommand joinCmd = new JoinGameCommand(gameId, bobId, "Bob");
        restTemplate.exchange(
                "/api/games/" + gameId + "/join", HttpMethod.POST, new HttpEntity<>(joinCmd, createHeaders(bobId)), GameResponseDto.class
        );

        int numberOfThreads = 50;
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        PlayTurnCommand playCmd = new PlayTurnCommand(gameId, aliceId, "ROCK");
        HttpEntity<PlayTurnCommand> playEntity = new HttpEntity<>(playCmd, createHeaders(aliceId));

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    restTemplate.exchange("/api/games/" + gameId + "/turns", HttpMethod.POST, playEntity, GameResponseDto.class);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        ResponseEntity<GameResponseDto> finalState = restTemplate.exchange(
                "/api/games/" + gameId, HttpMethod.GET, new HttpEntity<>(createHeaders(bobId)), GameResponseDto.class
        );
        GameResponseDto game = finalState.getBody();

        assertNotNull(game);
        assertEquals(GameStatus.IN_PROGRESS.name(), game.status());

        assertTrue(game.hasOpponentMoved());
        assertFalse(game.hasUserMoved());
        assertEquals(0, game.history().size());
        assertEquals(0, game.scorePlayer1());
        assertEquals(0, game.scorePlayer2());
    }

    private HttpHeaders createHeaders(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User-Id", userId);
        headers.set("X-Guest-Id", userId);
        return headers;
    }
}