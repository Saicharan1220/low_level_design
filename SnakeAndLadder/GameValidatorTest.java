package com.snakeandladder.model;

import com.snakeandladder.model.exception.InvalidBoardException;
import com.snakeandladder.model.exception.InvalidPlayerException;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class GameValidatorTest {
    
    @Test
    public void testValidateDiceConfigWithValidDice() {
        // Should not throw
        try {
            GameValidator.validateDiceConfig(2);
        } catch (Exception e) {
            fail("Valid dice configuration should not throw");
        }
    }
    
    @Test
    public void testValidateDiceConfigWithInvalidDice() {
        assertThrows(InvalidBoardException.class, () -> {
            GameValidator.validateDiceConfig(0);
        });
        
        assertThrows(InvalidBoardException.class, () -> {
            GameValidator.validateDiceConfig(7);
        });
    }
    
    @Test
    public void testValidatePlayerWithNullPlayer() {
        Queue<Player> players = new LinkedList<>();
        players.add(null);
        
        assertThrows(InvalidPlayerException.class, () -> {
            GameValidator.validatePlayers(players);
        });
    }
    
    @Test
    public void testValidatePlayerWithDuplicateIds() {
        Queue<Player> players = new LinkedList<>();
        players.add(new Player(1, "Player1"));
        players.add(new Player(1, "Player2"));  // Duplicate ID
        
        assertThrows(InvalidPlayerException.class, () -> {
            GameValidator.validatePlayers(players);
        });
    }
}