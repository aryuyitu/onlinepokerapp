package com.example.backend.DTOS;

public class Transition {
    private final boolean join; // if true then someone joined, if false then they left
    private final int playerId;
    private final String username;
    private final int chips;

    public Transition(int playerId, String username, int chips){
        this.join = true;
        this.playerId = playerId;
        this.chips = chips;
        this.username = username;
    }

    public Transition(int playerId){
        this.join = false;
        this.playerId = playerId;
        this.username = "";
        this.chips = 0;
    }

    public boolean joining(){
        return join;
    }

    public int getId(){
        return playerId;
    }

    public String getUsername(){
        return username;
    }

    public int getChips(){
        return chips;
    }
}
