package com.example.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.DTOS.PlayerInfo;
import com.example.backend.model.Player;
import com.example.backend.repository.PlayerRepository;

@Service
public class PlayerService {
    private final int defaultChipCount = 1000;
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository){
        this.playerRepository = playerRepository;
    }

    public List<Player> getPlayers(){
        return playerRepository.findAll();
    }

    public void addNewPlayer(String username, String password, String email) {
        
		Optional<Player> playerOptional = playerRepository.findPlayerByUsername(username);
		if (playerOptional.isPresent()){
			throw new IllegalStateException("username taken");
		}
        playerOptional = playerRepository.findPlayerByEmail(email);
		if (playerOptional.isPresent()){
			throw new IllegalStateException("email taken");
		}
        Player newPlayer = new Player(email,username,password,defaultChipCount);
        playerRepository.save(newPlayer);
	}

    public void deletePlayer(int playerID) {
		if(!playerRepository.existsById(playerID)){
			throw new IllegalStateException("student with id " + playerID + " does not exist.");
		}
		playerRepository.deleteById(playerID);
	}

    public void takeChips(int playerID, int chipAmount){
        Player player = playerRepository.findById(playerID).orElseThrow(() -> new IllegalStateException(
            "player does not exist"
        ));
        if(player.getChipReserve() > chipAmount){
            player.setChipReserve(player.getChipReserve() - chipAmount);
        } else {
            throw new IllegalStateException("not enough chips");
        }
    }

    public void addToGame(int playerID, int gameId){
        Player player = playerRepository.findById(playerID).orElseThrow(() -> new IllegalStateException(
            "player does not exist"
        ));
        player.setGameID(gameId);
    }

    public void removeFromGame(int playerID){
        Player player = playerRepository.findById(playerID).orElseThrow(() -> new IllegalStateException(
            "player does not exist"
        ));
        player.setGameID(0);
    }

    public String getPlayerName(int playerID){
        Player player = playerRepository.findById(playerID).orElseThrow(() -> new IllegalStateException(
            "player does not exist"
        ));
        return player.getUsername();
    }

    public PlayerInfo playerLogin(String username, String password){
        Optional<Player> playerOptional = playerRepository.findPlayerByUsername(username);
        if (playerOptional.isPresent()){
			Player player = playerOptional.get();
            if(player.getPassword().equals(password)){
                System.out.println("found player");
                return new PlayerInfo(username, player.getId(), player.getChipReserve());
            }
		}
        return new PlayerInfo("", -1, 0);
    }

    public PlayerInfo playerRegister(String username, String password, String email){
        Optional<Player> playerOptionalUsername = playerRepository.findPlayerByUsername(username);
        if (playerOptionalUsername.isPresent()){
			return new PlayerInfo("", -3, 0);
		}
        Optional<Player> playerOptionalEmail = playerRepository.findPlayerByEmail(email);
        if (playerOptionalEmail.isPresent()){
            return new PlayerInfo("", -4, 0);
        } 
        Player newPlayer = new Player(email,username,password,defaultChipCount);
        playerRepository.save(newPlayer);
        System.out.println("created user: " + newPlayer.getId());
        return new PlayerInfo(username, newPlayer.getId(), defaultChipCount);
    }
}
