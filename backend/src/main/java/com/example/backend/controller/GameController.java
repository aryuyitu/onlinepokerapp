package com.example.backend.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.DTOS.Move;
import com.example.backend.DTOS.Transition;
import com.example.backend.service.GameService;
import com.example.backend.service.PlayerService;

@RestController
@RequestMapping(path = "api/v1/game")
public class GameController {
    
    private final GameService gameService;
    private final PlayerService playerService;

    @Autowired
    public GameController(GameService gameService, PlayerService playerService){
        this.gameService = gameService;
        this.playerService = playerService;
    }

    //--------------------------- BELOW THIS WILL HANDLE JOINING GAMES -------------------------- //
    private final ConcurrentHashMap<Integer, CopyOnWriteArrayList<CompletableFuture<Transition>>> transitionRequests = new ConcurrentHashMap<>();

    @GetMapping("/poll-transition")
    public CompletableFuture<Transition> pollJoin(@RequestParam int gameId, @RequestParam String playerId) {
        CompletableFuture<Transition> future = new CompletableFuture<>();
        transitionRequests.computeIfAbsent(gameId, k -> new CopyOnWriteArrayList<>()).add(future);

        // Optional: Add a timeout to avoid hanging indefinitely
        return future.orTimeout(45, TimeUnit.SECONDS).whenComplete((result, error) -> {
            // Remove the request from the list after it's completed
            transitionRequests.get(gameId).remove(future);
            if (transitionRequests.get(gameId).isEmpty()) {
            transitionRequests.remove(gameId);
            }
        });
    }

    //call this to complete someone leaving or joining a game
    public void userTransition(int gameId, Transition newState) {
        // Notify all clients waiting for this game's updates
        CopyOnWriteArrayList<CompletableFuture<Transition>> futures = transitionRequests.get(gameId);
        if (futures != null) {
            for (CompletableFuture<Transition> future : futures) {
                future.complete(newState);
            }
        }
    }

    @GetMapping("/join")
    public void userJoining(@RequestParam int playerId, @RequestParam int chips){
        //call the game service and let them do the algorithm for joining
        int gameId = gameService.addToRandomGame(playerId, chips);
        Transition transition = new Transition(playerId,playerService.getPlayerName(playerId),chips);
        userTransition(gameId, transition);

        //still need to send smth to the actual user who sent this
    }

    @PutMapping("/leave")
    public void userLeaving(@RequestParam int gameId, @RequestParam int playerId){
        //call the game service and let them do the algorithm for leaving
    }







    //--------------------------- ABOVE THIS WILL HANDLE JOINING GAMES -------------------------- //
    //------------------------ BELOW THIS WILL HANDLE ALL THE GAME LOGIC ------------------------ //
    
    //this function is for polling and will be called by players when they are waiting for responses from the server

    private final ConcurrentHashMap<Integer, CopyOnWriteArrayList<CompletableFuture<Move>>> gameRequests = new ConcurrentHashMap<>();

    @GetMapping("/poll-moves")
    public CompletableFuture<Move> pollMove(@RequestParam int gameId, @RequestParam String playerId) {
        CompletableFuture<Move> future = new CompletableFuture<>();
        gameRequests.computeIfAbsent(gameId, k -> new CopyOnWriteArrayList<>()).add(future);

        // Optional: Add a timeout to avoid hanging indefinitely
        return future.orTimeout(45, TimeUnit.SECONDS).whenComplete((result, error) -> {
            // Remove the request from the list after it's completed
            gameRequests.get(gameId).remove(future);
            if (gameRequests.get(gameId).isEmpty()) {
                gameRequests.remove(gameId);
            }
        });
    }

    // Method to be called when a user performs an action
    public void userActionPerformed(int gameId, Move newState) {
        // Notify all clients waiting for this game's updates
        CopyOnWriteArrayList<CompletableFuture<Move>> futures = gameRequests.get(gameId);
        if (futures != null) {
            for (CompletableFuture<Move> future : futures) {
                future.complete(newState);
            }
        }
    }

    @PutMapping("/move")
    public void playerMoved(@RequestParam int gameId, @RequestBody Move move){
        userActionPerformed(gameId, move);
        switch(move.getMove()){
            case 'C' -> {
            }
            case 'F' -> {
            }
            case 'R' -> {
            }
            case 'K' -> {
            }
        }
    }
}
