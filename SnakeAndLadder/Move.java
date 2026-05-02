package com.snakeandladder.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Move {
    private final Player player;
    private final int diceRoll;
    private final int fromPosition;
    private final int toPosition;
    private final boolean landedOnSnake;
    private final boolean landedOnLadder;
    private final LocalDateTime timestamp;
    
    private Move(Builder builder) {
        this.player = builder.player;
        this.diceRoll = builder.diceRoll;
        this.fromPosition = builder.fromPosition;
        this.toPosition = builder.toPosition;
        this.landedOnSnake = builder.landedOnSnake;
        this.landedOnLadder = builder.landedOnLadder;
        this.timestamp = builder.timestamp;
    }
    
    // Getters
    public Player getPlayer() {
        return player;
    }
    
    public int getDiceRoll() {
        return diceRoll;
    }
    
    public int getFromPosition() {
        return fromPosition;
    }
    
    public int getToPosition() {
        return toPosition;
    }
    
    public boolean isLandedOnSnake() {
        return landedOnSnake;
    }
    
    public boolean isLandedOnLadder() {
        return landedOnLadder;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Move{player=%s, diceRoll=%d, from=%d, to=%d, snake=%s, ladder=%s}",
            player.getName(), diceRoll, fromPosition, toPosition, landedOnSnake, landedOnLadder
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return diceRoll == move.diceRoll &&
               fromPosition == move.fromPosition &&
               toPosition == move.toPosition &&
               Objects.equals(player, move.player);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(player, diceRoll, fromPosition, toPosition);
    }
    
    // Builder pattern for cleaner object creation
    public static class Builder {
        private Player player;
        private int diceRoll;
        private int fromPosition;
        private int toPosition;
        private boolean landedOnSnake;
        private boolean landedOnLadder;
        private LocalDateTime timestamp;
        
        public Builder player(Player player) {
            this.player = player;
            return this;
        }
        
        public Builder diceRoll(int diceRoll) {
            this.diceRoll = diceRoll;
            return this;
        }
        
        public Builder fromPosition(int fromPosition) {
            this.fromPosition = fromPosition;
            return this;
        }
        
        public Builder toPosition(int toPosition) {
            this.toPosition = toPosition;
            return this;
        }
        
        public Builder landedOnSnake(boolean landedOnSnake) {
            this.landedOnSnake = landedOnSnake;
            return this;
        }
        
        public Builder landedOnLadder(boolean landedOnLadder) {
            this.landedOnLadder = landedOnLadder;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Move build() {
            if (player == null) throw new IllegalStateException("Player is required");
            if (diceRoll < 1 || diceRoll > 12) 
                throw new IllegalStateException("Invalid dice roll");
            if (fromPosition < 0 || toPosition > 100)
                throw new IllegalStateException("Invalid positions");
            
            return new Move(this);
        }
    }
}