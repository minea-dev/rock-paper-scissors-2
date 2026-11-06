package com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence;

import com.mlicer.rockpaperscissors.application.port.out.GameRepository;
import com.mlicer.rockpaperscissors.domain.model.Game;
import com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence.entity.GameEntity;
import com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence.mapper.GameMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaGameRepositoryAdapter implements GameRepository {

    private final SpringDataJpaGameRepository jpaRepository;
    private final GameMapper gameMapper;

    public JpaGameRepositoryAdapter(SpringDataJpaGameRepository jpaRepository, GameMapper gameMapper) {
        this.jpaRepository = jpaRepository;
        this.gameMapper = gameMapper;
    }

    @Override
    public void save(Game game) {
        GameEntity entity = gameMapper.toEntity(game);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Game> findById(String id) {
        return jpaRepository.findById(id)
                .map(gameMapper::toDomain);
    }

    @Override
    public void delete(String id) {
        jpaRepository.deleteById(id);
    }
}