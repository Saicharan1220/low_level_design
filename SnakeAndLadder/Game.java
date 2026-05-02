package com.snakeandladder.model;

import com.snakeandladder.model.GameConfig.GameState;
import com.snakeandladder.model.exception.InvalidGameStateException;

import java.time.LocalDateTime;
import java.util.Queue;

public class Game {
    private String winner;
    private GameState gameState;
    private final Board board;
    private final GameHistory gameHistory;
    
    public Game(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        this.board = board;
        this.gameState = GameState.NOT_STARTED;
        this.gameHistory = new GameHistory();
    }
    
    public String getWinner() {
        return winner;
    }
    
    public GameState getGameState() {
        return gameState;
    }
    
    public GameHistory getGameHistory() {
        return gameHistory;
    }
    
    /**
     * Starts the game
     * @throws InvalidGameStateException if game is already in progress or completed
     */
    public void start() throws InvalidGameStateException {
        if (gameState == GameState.IN_PROGRESS) {
            throw new InvalidGameStateException("Game is already in progress");
        }
        if (gameState == GameState.COMPLETED) {
            throw new InvalidGameStateException("Game is already completed. Winner: " + winner);
        }
        
        gameState = GameState.IN_PROGRESS;
        GameLogger.gameEvent("=== GAME STARTED ===");
        GameLogger.info("Total players: " + board.getTotalPlayers());
        
        Queue<Player> players = board.getPlayers();
        
        while (gameState == GameState.IN_PROGRESS && !players.isEmpty()) {
            Player currentPlayer = players.poll();
            
            if (currentPlayer.getCurrentPosition() >= GameConfig.WINNING_POSITION) {
                // Player has already won, skip
                continue;
            }
            
            playTurn(currentPlayer, players);
        }
        
        gameState = GameState.COMPLETED;
        gameHistory.setGameEndTime(LocalDateTime.now());
        GameLogger.gameEvent("=== GAME COMPLETED ===");
        printGameSummary();
    }
    
    /**
     * Plays a single turn for a player
     * @param player the player whose turn it is
     * @param players the queue of players
     */
    private void playTurn(Player player, Queue<Player> players) {
        GameLogger.gameEvent(player.getName() + " is rolling the dice");
        
        int diceRoll = Dice.rollMultipleDice(board.getNumDice());
        int oldPosition = player.getCurrentPosition();
        int newPosition = oldPosition + diceRoll;
        
        GameLogger.info(String.format("%s rolled: %d", player.getName(), diceRoll));
        GameLogger.info(String.format("%s moved from %d to %d", 
            player.getName(), oldPosition, newPosition));
        
        // Check if player exceeds board size
        if (newPosition > GameConfig.WINNING_POSITION) {
            GameLogger.warn(String.format("%s rolled over 100 (rolled to %d), staying at %d", 
                player.getName(), newPosition, oldPosition));
            players.add(player);
            return;
        }
        
        // Check for snake or ladder
        if (board.hasSnakeOrLadder(newPosition)) {
            int finalPosition = board.getDestinationForPosition(newPosition);
            
            if (board.isLadder(newPosition, finalPosition)) {
                GameLogger.gameEvent(String.format("%s found a LADDER! Moving from %d to %d", 
                    player.getName(), newPosition, finalPosition));
                player.incrementLaddersClimbed();
            } else {
                GameLogger.gameEvent(String.format("%s encountered a SNAKE! Falling from %d to %d", 
                    player.getName(), newPosition, finalPosition));
                player.incrementSnakesEncountered();
            }
            newPosition = finalPosition;
        }
        
        // Check for winning condition
        if (newPosition == GameConfig.WINNING_POSITION) {
            winner = player.getName();
            player.setCurrentPosition(newPosition);
            gameState = GameState.COMPLETED;
            GameLogger.gameEvent("🎉 " + player.getName() + " WON THE GAME! 🎉");
            
            // Record the winning move
            recordMove(player, diceRoll, oldPosition, newPosition);
            return;
        }
        
        // Update player position and add back to queue
        player.setCurrentPosition(newPosition);
        player.incrementTurnsPlayed();
        players.add(player);
        
        // Record the move
        recordMove(player, diceRoll, oldPosition, newPosition);
    }
    
    /**
     * Records a move in the game history
     * @param player the player who made the move
     * @param diceRoll the dice roll value
     * @param fromPosition the starting position
     * @param toPosition the ending position
     */
    private void recordMove(Player player, int diceRoll, int fromPosition, int toPosition) {
        boolean onSnake = board.hasSnakeOrLadder(toPosition) && board.isSnake(fromPosition, toPosition);
        boolean onLadder = board.hasSnakeOrLadder(toPosition) && board.isLadder(fromPosition, toPosition);
        
        Move move = new Move.Builder()
                .player(player)
                .diceRoll(diceRoll)
                .fromPosition(fromPosition)
                .toPosition(toPosition)
                .landedOnSnake(onSnake)
                .landedOnLadder(onLadder)
                .timestamp(LocalDateTime.now())
                .build();
        
        gameHistory.recordMove(move);
    }
    
    /**
     * Prints a summary of the game
     */
    private void printGameSummary() {
        GameLogger.info("=== GAME SUMMARY ===");
        GameLogger.info("Winner: " + winner);
        GameLogger.info("Total moves: " + gameHistory.getTotalMoves());
        GameLogger.info("Game duration: " + calculateGameDuration() + " seconds");
        GameLogger.info("===================");
    }
    
    /**
     * Calculates the duration of the game in seconds
     * @return game duration in seconds
     */
    private long calculateGameDuration() {
        LocalDateTime endTime = gameHistory.getGameEndTime();
        LocalDateTime startTime = gameHistory.getGameStartTime();
        
        if (endTime == null) {
            return 0;
        }
        
        return java.time.temporal.ChronoUnit.SECONDS.between(startTime, endTime);
    }
    
    @Override
    public String toString() {
        return String.format("Game{winner=%s, state=%s, board=%s}", 
            winner, gameState, board);
    }
}