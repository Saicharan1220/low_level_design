package com.snakeandladder.model;

import com.snakeandladder.model.exception.InvalidBoardException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class SnakeAndLadderGame {
    
    public static void main(String[] args) {
        try {
            GameLogger.info("Starting Snake and Ladder Game...");
            
            // Create players
            Queue<Player> players = createPlayers();
            
            // Create snakes and ladders configuration
            Map<Integer, Integer> snakeAndLadderConfig = createSnakesAndLaddersConfig();
            
            // Create board
            Board board = new Board(2, snakeAndLadderConfig, players);
            
            // Create and start game
            Game game = new Game(board);
            game.start();
            
            // Print final results
            printGameResults(game);
            
        } catch (InvalidBoardException e) {
            GameLogger.error("Invalid board configuration: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            GameLogger.error("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates players for the game
     * @return queue of players
     */
    private static Queue<Player> createPlayers() {
        Queue<Player> playerQueue = new LinkedList<>();
        
        playerQueue.add(new Player(1, "Sai"));
        playerQueue.add(new Player(2, "Ram"));
        playerQueue.add(new Player(3, "Suku"));
        
        GameLogger.info("Created " + playerQueue.size() + " players");
        return playerQueue;
    }
    
    /**
     * Creates snakes and ladders configuration
     * @return map of positions and destinations
     */
    private static Map<Integer, Integer> createSnakesAndLaddersConfig() {
        Map<Integer, Integer> config = new HashMap<>();
        
        // Ladders (going up)
        config.put(3, 8);
        config.put(9, 30);
        config.put(16, 40);
        config.put(50, 80);
        config.put(33, 76);
        
        // Snakes (going down)
        config.put(26, 8);
        config.put(49, 30);
        config.put(56, 40);
        config.put(92, 80);
        config.put(86, 76);
        
        GameLogger.info("Configured " + config.size() + " snakes and ladders");
        return config;
    }
    
    /**
     * Prints the final game results
     * @param game the completed game
     */
    private static void printGameResults(Game game) {
        GameLogger.gameEvent("=== FINAL RESULTS ===");
        GameLogger.info("Winner: " + game.getWinner());
        GameLogger.info("Game State: " + game.getGameState());
        GameLogger.info("Total Moves Recorded: " + game.getGameHistory().getTotalMoves());
        GameLogger.gameEvent("====================");
    }
}