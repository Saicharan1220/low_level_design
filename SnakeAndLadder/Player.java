package com.snakeandladder.model;

import java.util.Objects;

public class Player {
    private final int id;
    private final String name;
    private int currentPosition;
    private int totalTurnsPlayed;
    private int snakesEncountered;
    private int laddersClimbed;
    
    public Player(int id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("Player ID must be positive");
        }
        
        this.id = id;
        this.name = name.trim();
        this.currentPosition = GameConfig.STARTING_POSITION;
        this.totalTurnsPlayed = 0;
        this.snakesEncountered = 0;
        this.laddersClimbed = 0;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getCurrentPosition() {
        return currentPosition;
    }
    
    public int getTotalTurnsPlayed() {
        return totalTurnsPlayed;
    }
    
    public int getSnakesEncountered() {
        return snakesEncountered;
    }
    
    public int getLaddersClimbed() {
        return laddersClimbed;
    }
    
    // Setters with validation
    public void setCurrentPosition(int position) {
        if (position < GameConfig.STARTING_POSITION || position > GameConfig.BOARD_SIZE) {
            throw new IllegalArgumentException(
                String.format("Position must be between %d and %d", 
                    GameConfig.STARTING_POSITION, GameConfig.BOARD_SIZE)
            );
        }
        this.currentPosition = position;
    }
    
    public void incrementTurnsPlayed() {
        this.totalTurnsPlayed++;
    }
    
    public void incrementSnakesEncountered() {
        this.snakesEncountered++;
    }
    
    public void incrementLaddersClimbed() {
        this.laddersClimbed++;
    }
    
    public void resetForNewGame() {
        this.currentPosition = GameConfig.STARTING_POSITION;
        this.totalTurnsPlayed = 0;
        this.snakesEncountered = 0;
        this.laddersClimbed = 0;
    }
    
    @Override
    public String toString() {
        return String.format("Player{id=%d, name='%s', position=%d, turns=%d, snakes=%d, ladders=%d}",
            id, name, currentPosition, totalTurnsPlayed, snakesEncountered, laddersClimbed);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}