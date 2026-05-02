package com.snakeandladder.model;

public class GameConfig {
    // Board dimensions
    public static final int BOARD_SIZE = 100;
    public static final int WINNING_POSITION = 100;
    
    // Dice configuration
    public static final int DICE_MIN_VALUE = 1;
    public static final int DICE_MAX_VALUE = 6;
    public static final int DEFAULT_NUMBER_OF_DICE = 2;
    
    // Game rules
    public static final int MINIMUM_PLAYERS = 2;
    public static final int MAXIMUM_PLAYERS = 10;
    public static final int STARTING_POSITION = 0;
    
    // Game states
    public enum GameState {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
    
    private GameConfig() {
        // Private constructor to prevent instantiation
    }
}