package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity(name = "Player")
@Table
public class Player {

    @Id
    @SequenceGenerator(
        name = "player_sequence",
        sequenceName = "player_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "student_sequence"
    )
    private int id;

    private String username;
    private String password;
    private String email;
    private int chipReserve;
    private int gameID;

    public Player(){

    }

    //if i have an id for them:
    //also note that chipReserve will always be in the constructor, but can be set in the creation of one.
    public Player(int id, String username, String email, String password, int chipReserve){ //if i have an id for them:
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.chipReserve = chipReserve;
        this.gameID = 0;
    }

    public Player(String email, String username, String password, int chipReserve){
        this.email = email;
        this.username = username;
        this.password = password;
        this.chipReserve = chipReserve;
        this.gameID = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getChipReserve() {
        return chipReserve;
    }

    public void setChipReserve(int chipReserve) {
        this.chipReserve = chipReserve;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    

}
