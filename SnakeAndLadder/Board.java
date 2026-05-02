package com.snakeandladder.model;

import com.snakeandladder.model.exception.InvalidBoardException;
import com.snakeandladder.model.exception.InvalidPlayerException;

import java.util.*;

public class Board {
    private final int numDice;
    private final Map<Integer, Integer> snakesAndLadders;
    private final Queue<Player> players;
    
    public Board(int numDice, Map<Integer, Integer> snakesAndLadders, Queue<Player> players)
            throws InvalidBoardException, InvalidPlayerException {
        // Validate all inputs
        GameValidator.validateDiceConfig(numDice);
        GameValidator.validateSnakesAndLaddersConfig(snakesAndLadders);
        GameValidator.validatePlayers(players);
        
        this.numDice = numDice;
        this.snakesAndLadders = Collections.unmodifiableMap(new HashMap<>(snakesAndLadders));
        this.players = new LinkedList<>(players);
    }
    
    public int getNumDice() {
        return numDice;
    }
    
    public Map<Integer, Integer> getSnakesAndLadders() {
        return snakesAndLadders;
    }
    
    public Queue<Player> getPlayers() {
        return new LinkedList<>(players);
    }
    
    public int getTotalPlayers() {
        return players.size();
    }
    
    public boolean hasSnakeOrLadder(int position) {
        return snakesAndLadders.containsKey(position);
    }
    
    public int getDestinationForPosition(int position) {
        return snakesAndLadders.getOrDefault(position, position);
    }
    
    public boolean isSnake(int fromPosition, int toPosition) {
        return toPosition < fromPosition;
    }
    
    public boolean isLadder(int fromPosition, int toPosition) {
        return toPosition > fromPosition;
    }
    
    @Override
    public String toString() {
        return String.format("Board{numDice=%d, snakesAndLadders=%d, players=%d}",
            numDice, snakesAndLadders.size(), players.size());
    }
}