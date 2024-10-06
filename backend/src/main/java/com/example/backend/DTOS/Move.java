package com.example.backend.DTOS;

public class Move {
    private final char moveType; //C: check. F: fold. R: raise. K: call the raise amount;
    private final int raiseAmount;

    public Move(char moveType, int raiseAmount){
        this.moveType = moveType;
        this.raiseAmount = raiseAmount;
    }

    public char getMove(){
        return this.moveType;
    }

    public int getRaiseAmount(){
        return this.raiseAmount;
    }
}
