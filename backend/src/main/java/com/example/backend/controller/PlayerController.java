package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.DTOS.PlayerInfo;
import com.example.backend.model.Player;
import com.example.backend.service.PlayerService;

//this controller will mostly handle player creation
// also handle chip reserve, but will not handle putting players into games
@RestController
@RequestMapping(path = "api/v1/player")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService){
        this.playerService = playerService;
    }

    @GetMapping
    public List<Player> getPlayers(){
        return playerService.getPlayers();
    }

    @GetMapping(value = "/login", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlayerInfo> login(@RequestParam String username, @RequestParam String password){
        return ResponseEntity.ok(playerService.playerLogin(username, password));
    }

    @PostMapping(value = "/register", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlayerInfo> register(@RequestParam String email, @RequestParam String username, @RequestParam String password){
        return ResponseEntity.ok(playerService.playerRegister(username, password, email));
    }

}
