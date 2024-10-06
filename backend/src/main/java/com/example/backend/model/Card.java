package com.example.backend.model;

public class Card {
    private final int suit; // 0:Clubs , 1:Diamonds , 2:Hearts , 3:Spades
    private final int rank; // 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 //12 being the ace, 0 being 2

    public Card(int suit, int rank){
        this.suit = suit;
        this.rank = rank;
    }

    public int getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }
    // this format should be used on the front end as well, so i can send numbers instead of strings via apis
    private static final String[] RANKS = {
        "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"
    };

    private static final String[] SUITS = {
        "Clubs", "Diamonds", "Hearts", "Spades"
    };

    @Override
    public String toString() {
        return RANKS[rank] + " of " + SUITS[suit];
    }

}
