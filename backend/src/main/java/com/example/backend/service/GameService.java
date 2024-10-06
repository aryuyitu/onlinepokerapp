package com.example.backend.service;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.example.backend.model.Game;

// this will be where all the games are held
// this will handle everytbning game related,
// especially players joining and leaving games
@Service
public class GameService {
    private final HashMap<Integer, Game> games = new HashMap<>();
    private int nextGameId = 1;
    private final int defaultSmallBlind = 10;

    public Game startNewGame(int smallBlind, int bigBlind){
        Game newGame = new Game(nextGameId++,smallBlind,bigBlind,7);
        games.put(newGame.getId(),newGame);
        return newGame;
    }

    public boolean addPlayerToGame(int gameId, int playerId, int chips){
        Game game = games.get(gameId);
        if(game != null){
            if(!game.isFull()){
                game.addPlayer(playerId, chips);
                return true;
            }
        }
        return false;
    }

    public int addToRandomGame(int playerId, int chips){
        int gameId = 0;
        for(Game game : games.values()){
            if(!game.isFull()){
                game.addPlayer(playerId, chips);
                gameId = game.getId();
                break;
            }
        }
        if(gameId == 0){ // this is when all games are full or no games exist
            Game newGame = startNewGame(this.defaultSmallBlind, this.defaultSmallBlind*2);
            newGame.addPlayer(playerId, chips);
            gameId = newGame.getId();
        }
        return gameId;
    }

    public void removePlayerFromGame(int playerId, int gameId){
        // find the game
        // remove them
        // if the game is empty, delete the game
        Game game = games.get(gameId);
        if(game != null){
            game.removePlayer(playerId);
        }
        if(game.getPlayerCount() == 0){
            games.remove(gameId);
        }
    }

    public void check(int gameId){
        Game game = games.get(gameId);
        if(game != null){
            game.check();
        }
    }

    public void raise(int gameId, int amount){
        Game game = games.get(gameId);
        if(game != null){
            game.raise(amount);
        }
    }

    public void call(int gameId){
        Game game = games.get(gameId);
        if(game != null){
            game.call();
        }
    }
    
    public int fold(int gameId){
        Game game = games.get(gameId);
        if(game != null){
            game.fold();
            return game.getActionId();
        } else {
            return 0;
        }
    }
}
