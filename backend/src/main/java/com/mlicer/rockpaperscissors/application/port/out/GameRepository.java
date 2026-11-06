package com.mlicer.rockpaperscissors.application.port.out;

import com.mlicer.rockpaperscissors.domain.model.Game;

import java.util.Optional;

public interface GameRepository {

    void save(Game game);

    Optional<Game> findById(String id);

    void delete(String id);
}
