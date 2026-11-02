package com.mlicer.rockpaperscissors.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rounds")
public class RoundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(name = "player1_move", nullable = false)
    private String player1Move;

    @Column(name = "player2_move", nullable = false)
    private String player2Move;

    @Column(name = "result", nullable = false)
    private String result;

    protected RoundEntity() {}

    public RoundEntity(GameEntity game, int roundNumber, String player1Move, String player2Move, String result) {
        this.game = game;
        this.roundNumber = roundNumber;
        this.player1Move = player1Move;
        this.player2Move = player2Move;
        this.result = result;
    }

    public Long getId() { return id; }
    public GameEntity getGame() { return game; }
    public int getRoundNumber() { return roundNumber; }
    public String getPlayer1Move() { return player1Move; }
    public String getPlayer2Move() { return player2Move; }
    public String getResult() { return result; }
}