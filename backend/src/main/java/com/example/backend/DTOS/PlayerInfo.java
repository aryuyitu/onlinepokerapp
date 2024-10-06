package com.example.backend.DTOS;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerInfo {
    @JsonProperty("playerId")
    private final int playerId;

    @JsonProperty("username")
    private final String username;
    
    @JsonProperty("chipReserve")
    private final int chipReserve;

    public PlayerInfo(String username, int playerId, int chipReserve){
        this.playerId = playerId;
        this.username = username;
        this.chipReserve = chipReserve;
    }
}
