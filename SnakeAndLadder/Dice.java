package com.snakeandladder.model;

import java.util.Random;

public class Dice {
    private static final Random RANDOM = new Random();
    
    private Dice() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Rolls a single die (1-6)
     * @return the result of a single die roll
     */
    public static int rollSingleDie() {
        return RANDOM.nextInt(GameConfig.DICE_MAX_VALUE - GameConfig.DICE_MIN_VALUE + 1) 
               + GameConfig.DICE_MIN_VALUE;
    }
    
    /**
     * Rolls multiple dice
     * @param numberOfDice number of dice to roll
     * @return sum of all dice rolls
     * @throws IllegalArgumentException if numberOfDice is invalid
     */
    public static int rollMultipleDice(int numberOfDice) {
        if (numberOfDice < 1 || numberOfDice > 6) {
            throw new IllegalArgumentException(
                String.format("Number of dice must be between 1 and 6, got: %d", numberOfDice)
            );
        }
        
        int totalRoll = 0;
        for (int i = 0; i < numberOfDice; i++) {
            totalRoll += rollSingleDie();
        }
        return totalRoll;
    }
    
    /**
     * Rolls dice and returns individual results
     * @param numberOfDice number of dice to roll
     * @return array of individual die rolls
     */
    public static int[] rollDiceDetailed(int numberOfDice) {
        if (numberOfDice < 1 || numberOfDice > 6) {
            throw new IllegalArgumentException(
                String.format("Number of dice must be between 1 and 6, got: %d", numberOfDice)
            );
        }
        
        int[] rolls = new int[numberOfDice];
        for (int i = 0; i < numberOfDice; i++) {
            rolls[i] = rollSingleDie();
        }
        return rolls;
    }
}