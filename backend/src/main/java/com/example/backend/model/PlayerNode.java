package com.example.backend.model;

import java.util.ArrayList;
import java.util.List;

public class PlayerNode {
    public PlayerNode next;
    public int chipCount, callAmount;
    public int id;
    public boolean folded;
    public List<Card> hand;

    public PlayerNode(){

    }
    public PlayerNode(int id, int chipCount){
        this.id = id;
        this.chipCount = chipCount;
        this.folded = true;
        this.next = null;
        this.callAmount = 0;
        this.hand = new ArrayList<>();
    }

    public void dealCards(Card card1, Card card2){
        hand.clear();
        hand.add(card1);
        hand.add(card2);
    }

    public void reset(){
        folded = false;
        callAmount = 0;
    }

    public Card getCard(int cardNum){
        return hand.get(cardNum);
    }

    public void printPlayer(){ // this is for testing
        System.out.print(id + " (" + chipCount + (folded?", folded)":")"));
        if(hand.isEmpty()){
            System.out.println("");
        } else {
            System.out.println(" has " + hand.get(0).toString() + " and " + hand.get(1).toString());
        }
    }

    public void fold(){
        folded = true;
    }
}
