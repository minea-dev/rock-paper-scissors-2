package com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence.mapper;

import com.mlicer.rockpaperscissors.domain.model.*;
import com.mlicer.rockpaperscissors.domain.rules.ClassicRules;
import com.mlicer.rockpaperscissors.domain.rules.GameRules;
import com.mlicer.rockpaperscissors.domain.rules.SpockRules;
import com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence.entity.GameEntity;
import com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence.entity.RoundEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameMapper {

    public GameEntity toEntity(Game game) {
        GameEntity entity = new GameEntity(game.getId());

        entity.setPlayer1Id(game.getPlayer1().id());
        entity.setPlayer1Username(game.getPlayer1().username());

        if (game.getPlayer2() != null) {
            entity.setPlayer2Id(game.getPlayer2().id());
            entity.setPlayer2Username(game.getPlayer2().username());
        }

        entity.setRulesType(game.getRules() instanceof SpockRules ? "SPOCK" : "CLASSIC");

        entity.setWinningConditionTotalRounds(game.getWinningCondition().totalRounds());
        entity.setWinningConditionTargetScore(game.getWinningCondition().targetScore());

        entity.setScorePlayer1(game.getScorePlayer1());
        entity.setScorePlayer2(game.getScorePlayer2());
        entity.setStatus(game.getStatus().name());

        if (game.getMovePlayer1() != null) entity.setCurrentMovePlayer1(game.getMovePlayer1().name());
        if (game.getMovePlayer2() != null) entity.setCurrentMovePlayer2(game.getMovePlayer2().name());

        if (game.getPlayerWhoAbandoned() != null) {
            entity.setPlayerWhoAbandonedId(game.getPlayerWhoAbandoned().id());
        }

        List<RoundEntity> roundEntities = game.getRoundsHistory().stream()
                .map(r -> new RoundEntity(entity, r.roundNumber(), r.player1Move().name(), r.player2Move().name(), r.result().name()))
                .toList();

        roundEntities.forEach(entity::addRound);

        return entity;
    }

    public Game toDomain(GameEntity entity) {
        Player player1 = new Player(entity.getPlayer1Id(), entity.getPlayer1Username());
        Player player2 = entity.getPlayer2Id() != null
                ? new Player(entity.getPlayer2Id(), entity.getPlayer2Username())
                : null;

        GameRules rules = "SPOCK".equalsIgnoreCase(entity.getRulesType())
                ? new SpockRules()
                : new ClassicRules();

        WinningCondition condition = new WinningCondition(
                entity.getWinningConditionTotalRounds(),
                entity.getWinningConditionTargetScore()
        );

        Move move1 = entity.getCurrentMovePlayer1() != null ? Move.valueOf(entity.getCurrentMovePlayer1()) : null;
        Move move2 = entity.getCurrentMovePlayer2() != null ? Move.valueOf(entity.getCurrentMovePlayer2()) : null;

        Player abandoned = null;
        if (entity.getPlayerWhoAbandonedId() != null) {
            abandoned = entity.getPlayerWhoAbandonedId().equals(player1.id()) ? player1 : player2;
        }

        List<Round> history = entity.getRounds().stream()
                .map(r -> new Round(r.getRoundNumber(), Move.valueOf(r.getPlayer1Move()), Move.valueOf(r.getPlayer2Move()), Result.valueOf(r.getResult())))
                .toList();

        return Game.reconstitute(
                entity.getId(),
                player1,
                player2,
                rules,
                condition,
                entity.getScorePlayer1(),
                entity.getScorePlayer2(),
                move1,
                move2,
                GameStatus.valueOf(entity.getStatus()),
                abandoned,
                history
        );
    }
}