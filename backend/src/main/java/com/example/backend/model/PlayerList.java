package com.example.backend.model;

import java.util.Objects;

public class PlayerList{
    private PlayerNode head;
    private PlayerNode action;
    private int playerCount;
    private int position; // 0 indexed
    private int playersInPlay;

    public PlayerList(){
        this.head = null;
        this.action = this.head;
        this.position = 0;
        this.playerCount = 0;
        this.playersInPlay = 0;
    }

    public void add(PlayerNode player){
        player.next = this.head;
        this.head = player;
        ++this.playerCount;        
    }

    public boolean remove(int id){
        // returns true if a player was removed
        if(this.head == null){
            //it shouldnt even be run iof the playerList is empty, so think about removing this check
            return false;
        } else if (Objects.equals(this.head.id, id)){
            if(action == head){
                action = action.next;
            }
            this.head = this.head.next;
            --this.playerCount;
            return true;
        } else {
            int tempPosition = 0;
            PlayerNode current = this.head;
            while(current.next != null){
                if(Objects.equals(current.next.id, id)){
                    current.next = current.next.next;
                    --this.playerCount;
                    if(tempPosition < this.position){
                        this.position--;
                    }
                    return true;
                }
                current = current.next;
                tempPosition++;
            }
        }
        return false; // this is the case where no one is removed
    }

    public int getPlayerCount(){ // this can also function as isempty
        return this.playerCount;
    }

    public int moveAction(){ //will move the action
        if(action.next == null){
            action = head;
            position = 0;
        } else {
            action = action.next;
            position++;
        }
        return this.position;
    }

    public int getPosition(){
        return this.position;
    }
    
    public PlayerNode getAction(){
        return this.action;
    }

    public int getActionId(){
        return this.action.id;
    }

    public boolean getActionFold(){
        return this.action.folded;
    }

    /**
     * 
     * @return returns true if there is one more player that has not folded
     */
    public boolean foldAction(){
        System.out.println(this.action.id + " folded");
        this.action.fold();
        return (--playersInPlay) == 1;
    }

    public void actionBet(int chips){
        action.callAmount += chips;
        action.chipCount -= chips;
    }

    /**
     * 
     * @param callAmount is the TOTAL amount that one must call to stay in the pot
     */

    public int actionCall(int callAmount){
        int diff = callAmount - this.action.callAmount;
        actionBet(diff);
        return diff;
    }

    public void resetAction(){ // this is useful trust
        action = head;
        position = 0;
    }

    public void resetEveryone(){
        PlayerNode current = head;
        while(current != null){
            current.reset();
            current = current.next;
        }
        playersInPlay = playerCount;
    }

    public void resetCallAmounts(){
        PlayerNode current = head;
        while(current != null){
            current.callAmount = 0;
            current = current.next;
        }
    }

    public void printAllPlayers(){
        PlayerNode current = head;
        while(current != null){
            current.printPlayer();
            current = current.next;
        }
    }

    /**
     * @param windex if you were to remove every player that has folded from the list, the winner should be at the index of this modified list (zero-indexed)
     * 
     * 
     */

    public PlayerNode getWinner(int windex){
        PlayerNode current = head;
        while(current != null){
            if(current.folded){
                current = current.next;
            } else {
                break;
            }
        }
        for(int i = 0; i < windex;){
            while(current.folded){
                System.out.println("next");
                current = current.next;
            }
            current = current.next;
            ++i;
        }
        return current;
    }

    public int giveWinner(int windex, int chips){
        PlayerNode winner = getWinner(windex);
        winner.chipCount += chips;
        return winner.id;
    }
    // public int getPlayerChips(int id){

    // }

    public void bet(int chips){
        getAction().chipCount -= chips;
        getAction().callAmount += chips;
    }
    
}
