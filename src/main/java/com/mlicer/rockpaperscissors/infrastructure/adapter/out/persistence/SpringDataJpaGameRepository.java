package com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence;

import com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaGameRepository extends JpaRepository<GameEntity, String> {
}