package com.example.backend.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;
    private int index;

    public Deck() {
        cards = new ArrayList<>();
        final int[] suits = {0,1,2,3};
        final int[] ranks = {0,1,2,3,4,5,6,7,8,9,10,11,12};

        for(int suit: suits){
            for(int rank: ranks){
                cards.add(new Card(suit,rank));
            }
        }
        index = 0;

    }

    public void shuffle(){
       Collections.shuffle(cards);
       index = 0;
    }

    public Card dealCard(){
        Card card = cards.get(index++);
        // System.out.println(card.toString());
        return card;
    }

    public List<Card> getCards(){
        return cards;
    }
}
