package com.mlicer.rockpaperscissors.infrastructure.config;

import com.mlicer.rockpaperscissors.application.port.in.*;
import com.mlicer.rockpaperscissors.application.port.out.GameEventPublisher;
import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.application.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameUseCaseConfig {

    @Bean
    public CreateGameUseCase createGameUseCase(GameRepository gameRepository) {
        return new CreateGameService(gameRepository);
    }

    @Bean
    public JoinGameUseCase joinGameUseCase(GameRepository gameRepository, GameEventPublisher gameEventPublisher) {
        return new JoinGameService(gameRepository, gameEventPublisher);
    }

    @Bean
    public PlayTurnUseCase playTurnUseCase(GameRepository gameRepository, GameEventPublisher gameEventPublisher) {
        return new PlayTurnService(gameRepository, gameEventPublisher);
    }

    @Bean
    public AbandonGameUseCase abandonGameUseCase(GameRepository gameRepository, GameEventPublisher gameEventPublisher) {
        return new AbandonGameService(gameRepository, gameEventPublisher);
    }

    @Bean
    public GetGameUseCase getGameUseCase(GameRepository gameRepository) {
        return new GetGameService(gameRepository);
    }
}