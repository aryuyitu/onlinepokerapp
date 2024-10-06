package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		// RankPokerHandPublic.initRankPokerHand7(); // this can just be run here
		// int[] rank = {0, 0, 0, 1, 0, 12, 12};
        // int[] suit = {0, 1, 2, 0, 3, 0, 1};

		// int[] rank2 = {0, 12, 0, 0, 1, 0, 12};
        // int[] suit2 = {0, 0, 2, 1, 0, 3, 1};

        // int[] buffer = new int[4];
		// System.out.println("four-of-a-kind 2's with A kicker: " + RankPokerHandPublic.rankPokerHand7(rank, suit, buffer));
		// System.out.println("same hand: " + RankPokerHandPublic.rankPokerHand7(rank2, suit2, buffer));
	}

}
