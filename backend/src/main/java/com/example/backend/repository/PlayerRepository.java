package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    @Query("SELECT s FROM Player s WHERE s.username = ?1")
    Optional<Player> findPlayerByUsername(String username);

    @Query("SELECT s FROM Player s WHERE s.id = ?1")
    Optional<Player> findPlayerByID(int id);

    @Query("SELECT s FROM Player s WHERE s.email = ?1")
    Optional<Player> findPlayerByEmail(String email);
}
