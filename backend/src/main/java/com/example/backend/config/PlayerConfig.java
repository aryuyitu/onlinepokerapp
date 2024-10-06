package com.example.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.backend.model.Player;
import com.example.backend.repository.PlayerRepository;
import com.example.backend.service.PlayerService;

// this is a temp file and should be deleted in the future
@Configuration
public class PlayerConfig {
    @Bean
    CommandLineRunner commandLineRunner(PlayerRepository repository, PlayerService playerService){
        return args -> {
            Player alexander = new Player(
                "alexander.yuyitung@gmail.com",
                "alequander",
                "alexander1",
                1000
            );
            repository.save(alexander);
            System.out.println(playerService.playerRegister("alequander2", "bruh", "bruh@gmail.com"));
        };
    }
}
