package com.snakeandladder.model;

import com.snakeandladder.model.exception.InvalidBoardException;
import com.snakeandladder.model.exception.InvalidPlayerException;

import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GameValidator {
    
    private GameValidator() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Validates dice configuration
     * @param numDice number of dice to roll
     * @throws InvalidBoardException if configuration is invalid
     */
    public static void validateDiceConfig(int numDice) throws InvalidBoardException {
        if (numDice < 1 || numDice > 6) {
            throw new InvalidBoardException(
                String.format("Number of dice must be between 1 and 6, got: %d", numDice)
            );
        }
    }
    
    /**
     * Validates snakes and ladders configuration
     * @param snakesAndLadders map of positions to destinations
     * @throws InvalidBoardException if configuration is invalid
     */
    public static void validateSnakesAndLaddersConfig(Map<Integer, Integer> snakesAndLadders) 
            throws InvalidBoardException {
        if (snakesAndLadders == null) {
            throw new InvalidBoardException("Snakes and Ladders map cannot be null");
        }
        if (snakesAndLadders.isEmpty()) {
            throw new InvalidBoardException("Snakes and Ladders map cannot be empty");
        }
        
        for (Map.Entry<Integer, Integer> entry : snakesAndLadders.entrySet()) {
            int from = entry.getKey();
            int to = entry.getValue();
            
            if (from <= 0 || from >= GameConfig.WINNING_POSITION) {
                throw new InvalidBoardException(
                    String.format("Invalid 'from' position: %d. Must be between 1 and 99", from)
                );
            }
            if (to <= 0 || to >= GameConfig.WINNING_POSITION) {
                throw new InvalidBoardException(
                    String.format("Invalid 'to' position: %d. Must be between 1 and 99", to)
                );
            }
            if (from == to) {
                throw new InvalidBoardException(
                    String.format("Snake/Ladder cannot have same 'from' and 'to' position: %d", from)
                );
            }
        }
    }
    
    /**
     * Validates player list
     * @param players queue of players
     * @throws InvalidPlayerException if player list is invalid
     */
    public static void validatePlayers(Queue<Player> players) throws InvalidPlayerException {
        if (players == null) {
            throw new InvalidPlayerException("Players queue cannot be null");
        }
        if (players.isEmpty()) {
            throw new InvalidPlayerException(
                String.format("At least %d players are required", GameConfig.MINIMUM_PLAYERS)
            );
        }
        if (players.size() > GameConfig.MAXIMUM_PLAYERS) {
            throw new InvalidPlayerException(
                String.format("Maximum %d players allowed, got: %d", 
                    GameConfig.MAXIMUM_PLAYERS, players.size())
            );
        }
        
        Set<Integer> playerIds = new HashSet<>();
        for (Player player : players) {
            if (player == null) {
                throw new InvalidPlayerException("Player cannot be null");
            }
            if (player.getName() == null || player.getName().trim().isEmpty()) {
                throw new InvalidPlayerException("Player name cannot be empty");
            }
            if (playerIds.contains(player.getId())) {
                throw new InvalidPlayerException(
                    String.format("Duplicate player ID: %d", player.getId())
                );
            }
            playerIds.add(player.getId());
        }
    }
    
    /**
     * Validates if a position is valid on the board
     * @param position position to validate
     * @return true if position is valid
     */
    public static boolean isValidBoardPosition(int position) {
        return position >= GameConfig.STARTING_POSITION && position <= GameConfig.BOARD_SIZE;
    }
}