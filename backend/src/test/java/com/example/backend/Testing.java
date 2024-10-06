package com.example.backend;

import com.example.backend.model.Game;
import com.example.backend.service.RankPokerHandPublic;

public class Testing {

    public static void main(String[] args) {
        Game game = new Game(1, 5, 10, 7);
        RankPokerHandPublic.initRankPokerHand7();
        game.addPlayer(1,3000);
        game.addPlayer(2,3000);
        game.addPlayer(3,3000);
        game.nextPhase(); // get out of the waiting phase
        // for(int j = 0; j < 3; j++){
        //     game.nextPhase(); // get out of the waiting phase
        //     //here is when the action happens
        //     for (int i = 0; i < 4; i++){
        //         game.check();
        //         game.raise(10);
        //         game.call();
        //         game.call();
        //     }
        //     game.testStatus();
        //     game.nextPhase();
        // }

        game.raise(10);
        game.call();
        game.call();

        game.check();
        game.fold();
        game.removePlayer(1);

        game.nextPhase();

        game.call();
        game.raise(10);
        game.addPlayer(44,3000);
        game.call();

        game.check();
        game.check();

        game.raise(100);
        game.fold();


        game.testStatus();
        // for(int i = 1; i < 4 ; i++){
        //     game.check();
        //     game.check();
        //     game.check();
        // }
        // game.testStatus();
        
    }
}
