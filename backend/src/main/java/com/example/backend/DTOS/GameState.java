package com.example.backend.DTOS;

// this will be the object that is passed to the client after another player makes a move
// this will not be the thing sent when a new board card is released
// this can indicate when there is a change of phase
// 
public class GameState {
    public char move;       // the most recent move
    public int callAmount;  // the amount needed to call
    public int action;     // the player that has the action now

    public GameState(char move, int callAmount, int action){
        this.move = move;
        this.callAmount = callAmount;
        this.action = action;
    }
}
