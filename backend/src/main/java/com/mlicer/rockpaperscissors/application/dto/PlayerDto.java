package com.mlicer.rockpaperscissors.application.dto;

import com.mlicer.rockpaperscissors.domain.model.Player;

public record PlayerDto(String id, String username) {
    public static PlayerDto from(Player player) {
        if (player == null) return null;
        return new PlayerDto(player.id(), player.username());
    }
}