package com.snakeandladder.model;

import java.time.LocalDateTime;
import java.util.*;

public class GameHistory {
    private final List<Move> moves;
    private final LocalDateTime gameStartTime;
    private LocalDateTime gameEndTime;
    
    public GameHistory() {
        this.moves = new ArrayList<>();
        this.gameStartTime = java.time.LocalDateTime.now();
    }
    
    public void recordMove(Move move) {
        if (move == null) {
            throw new IllegalArgumentException("Move cannot be null");
        }
        moves.add(move);
    }
    
    public List<Move> getMoves() {
        return Collections.unmodifiableList(moves);
    }
    
    public List<Move> getPlayerMoves(Player player) {
        return moves.stream()
                .filter(move -> move.getPlayer().equals(player))
                .toList();
    }
    
    public int getTotalMoves() {
        return moves.size();
    }
    
    public void setGameEndTime(LocalDateTime endTime) {
        this.gameEndTime = endTime;
    }
    
    public LocalDateTime getGameStartTime() {
        return gameStartTime;
    }
    
    public LocalDateTime getGameEndTime() {
        return gameEndTime;
    }
    
    @Override
    public String toString() {
        return String.format("GameHistory{totalMoves=%d, startTime=%s, endTime=%s}",
            moves.size(), gameStartTime, gameEndTime);
    }
}