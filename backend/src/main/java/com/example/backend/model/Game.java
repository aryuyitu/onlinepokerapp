package com.example.backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.backend.service.RankPokerHandPublic;

public class Game {

    private PlayerList playerList;
    private int id;
    private int playerCount;
    private int maxPlayerCount;
    private int smallBlindPos; //the player who is the small blind will be zero indexed from the linkedlist
    private int gamePhase; //gameStates will be as follows: 0 waiting, 1 preflop, 2 flop, 3 turn, 4 river, 5 showdown or deciding a winner
    private int callAmount;
    private int potSize;
    private int smallBlind, bigBlind;
    private Deck deck;
    private List<Card> boardCards;

    private static final String[] PHASES = {
        "Waiting", "PreFlop", "Flop", "Turn", "River", "Showdown", "Waiting"
    };

    public Game(int id, int smallBlind, int bigBlind, int maxPlayerCount){
        this.id = id;
        this.playerCount = 0;
        this.smallBlindPos = 0;
        this.gamePhase = 0;
        this.callAmount = 0;
        this.potSize = 0;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.maxPlayerCount = maxPlayerCount;
        this.playerList = new PlayerList();
        this.deck = new Deck();
        this.boardCards = new ArrayList<>();
    }

    //these functions will have to be edited to account for the actual game and its state
    public void addPlayer(int id, int chips){
        System.out.println(id + " has joined the game.");
        this.playerList.add(new PlayerNode(id,chips));
        playerCount++;
    }

    public void removePlayer(int id){
        System.out.println("removing "+ id);
        if(Objects.equals(playerList.getActionId(), id)){
            fold();
        }
        if(playerList.remove(id)){
            playerCount--;
        }

        //also have to adjust the smallblindPos
        //also have 
    }

    public int getId() {
        return id;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getMaxPlayerCount(){
        return maxPlayerCount;
    }

    public boolean isFull(){
        return maxPlayerCount >= playerCount;
    }

    public void newRound(){
        playerList.resetEveryone();
        potSize = 0;
        this.callAmount = 0;
    }

    public void nextPhase(){
        System.out.println("next phase: " + PHASES[gamePhase+1]);
        callAmount = 0;
        playerList.resetCallAmounts();
        if(++gamePhase > 5){ // waiting, try to reset everything here
            gamePhase = 0;
            boardCards.clear();
            if (++smallBlindPos >= playerCount){
                smallBlindPos = 0;
            }
        } else {
        playerList.resetAction();
        for (int i = 0; i < smallBlindPos; i++){ // this is to get the action to the small blind player
            playerList.moveAction();
        }
        if (gamePhase == 1){
            newRound(); // i do this here to keep information stored during the waiting phase.
        }
        while(playerList.getActionFold()){ // skip over players that have folded
            playerList.moveAction();
        }
        switch (gamePhase){
            case 1 -> { //preflop
                deck.shuffle();
                for(int i = 0; i < playerCount; i++){ // deal cards to each player
                    playerList.getAction().dealCards(deck.dealCard(), deck.dealCard());
                    playerList.moveAction();
                }
                this.callAmount = bigBlind;
                playerList.actionBet(smallBlind);
                potSize += smallBlind;
                playerList.moveAction();
                playerList.actionBet(bigBlind);
                potSize += bigBlind;
                playerList.moveAction();
                break;
            }
            case 2 -> { //flop
                boardCards.add(deck.dealCard());
                boardCards.add(deck.dealCard());
                boardCards.add(deck.dealCard());
                break;
            }
            case 3 -> { //turn
                boardCards.add(deck.dealCard());
                break;
            }
            case 4 -> { // river
                boardCards.add(deck.dealCard());
                break;
            }
            case 5 -> { //showdown
                int windex = determineWinner();
                giveWinner(windex, potSize);

                // give the player all the chips
                break;
            }
            
            default -> {

                break;
            }
        }
        }
    }

    //this works as a good way to move the action to the next player
    public void check(){
        System.out.println(playerList.getActionId() + " checks");
        int position = playerList.moveAction();
        boolean nextPhase = false;
        if(position == smallBlindPos){
            nextPhase = true;
        }
        while(playerList.getActionFold()){
            if(playerList.moveAction() == smallBlindPos){
                nextPhase = true;
            }
        }
        if (nextPhase || (playerList.getAction().callAmount == this.callAmount && this.callAmount != 0)){ // this is for the case where the big blind checks during the preflop phase
            nextPhase();
        }
    }

    /**
     * @param chips is the amount you want to raise by, not including the call amount
     */
    public void raise(int chips){
        int diff = playerList.actionCall(this.callAmount);
        this.potSize += diff;
        this.potSize += chips;
        if(this.callAmount != 0){
            System.out.print(playerList.getActionId() + " calls " + this.callAmount + " and ");
        }
        System.out.println(playerList.getActionId() + " raises by " + chips + " (" + this.potSize + ")");
        this.callAmount += chips;
        playerList.actionBet(chips);
        playerList.moveAction();
        while(playerList.getActionFold()){
            playerList.moveAction();
        }
    }

    public void call(){
        int diff = playerList.actionCall(this.callAmount);
        this.potSize += diff;
        System.out.println(playerList.getActionId() + " calls " + this.callAmount + " (" + this.potSize + ")");
        playerList.moveAction();
        while(playerList.getActionFold()){
            playerList.moveAction();
        }
        if (playerList.getAction().callAmount == this.callAmount){
            //if the player is the big blind, and the call amount is still the same as the original big blind during the preflop, then you do not want to go next phase
            //so: if the phase is not 1
            int bigBlindPos = (smallBlindPos+1)%playerCount;
            if (!(playerList.getPosition() == bigBlindPos && gamePhase == 1 && this.callAmount == this.bigBlind)){
                nextPhase();
            }
        }
    }
    
    public void fold(){
        if(playerList.foldAction()){
            giveWinner(0,potSize);
            gamePhase = 0;
        } else {
            if(this.callAmount > 0){ // if they are in the middle of betting
                playerList.moveAction();
                while(playerList.getActionFold()){
                    playerList.moveAction();
                }
                if (playerList.getAction().callAmount == this.callAmount){
                    int bigBlindPos = (smallBlindPos+1)%playerCount;
                    if (!(playerList.getPosition() == bigBlindPos && gamePhase == 1 && this.callAmount == this.bigBlind)){
                        nextPhase();
                    }
                }
            } else { //if evberyone else was checking around
                int position = playerList.moveAction();
                boolean nextPhase = false;
                if(position == smallBlindPos){
                    nextPhase = true;
                }
                while(playerList.getActionFold()){
                    if(playerList.moveAction() == smallBlindPos){
                        nextPhase = true;
                    }
                }
                if(nextPhase){
                    nextPhase();
                }
            }
        }
    }

    public void giveWinner(int windex, int chips){
        int winnerId = playerList.giveWinner(windex, chips);
        System.out.println("the winner is: " + (winnerId));
    }

    public int determineWinner(){ // this can only be run when the board cards are full
        ArrayList<int[]> rankList = new ArrayList<>();
        ArrayList<int[]> suitList = new ArrayList<>();
        playerList.resetAction();
        for (int i = 0; ; i = playerList.moveAction()){ // putting everyones hand with board cards into a list
            PlayerNode player = playerList.getAction();
            if (!(player.folded)){ // if the person did not fold:
                int[] rankTemp = new int[7];
                int[] suitTemp = new int[7];
                for(int j = 0; j < 2; j++){
                    rankTemp[j] = player.getCard(j).getRank();
                    suitTemp[j] = player.getCard(j).getSuit();
                }
                for(int j = 2; j < 7 ; j++){
                    rankTemp[j] = boardCards.get(j-2).getRank();
                    suitTemp[j] = boardCards.get(j-2).getSuit();
                }
                rankList.add(rankTemp);
                suitList.add(suitTemp);
            }
            if(i + 1 == playerCount){
                break;
            }
        }
        int windex = 0; //hehehe (winIndex)
        int currentMax = 0;
        int[] buffer = new int[4];
        for(int i = 0; i < rankList.size(); i++){
            int temp = RankPokerHandPublic.rankPokerHand7(rankList.get(i), suitList.get(i), buffer);
            if(temp > currentMax){
                windex = i;
                currentMax = temp;
            }
        }
        return windex;
    }

    public List<Card> getBoardCards() {
        return boardCards;
    }

    public void testStatus(){
        playerList.printAllPlayers();
        System.out.println("The phase is: " + PHASES[gamePhase] + ", potsize: " + potSize);
        System.out.print("Board cards: ");
        for(Card card: boardCards){
            System.out.print(card.toString() + ", ");
        }
        System.out.println("");
        System.out.println("");
    }

    // this is for when the next player goes
    public int getActionId(){
        return playerList.getActionId();
    }
}
