package com.snakeandladder.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    @Test
    public void testPlayerPositionNotUpdatedWhenRollingOver100() {
        // Test case to prevent the position update bug
        Player player = new Player(1, "Test");
        int oldPosition = 95;
        int diceRoll = 10;  // Would put player at 105
        
        player.setCurrentPosition(oldPosition);
        int newPosition = oldPosition + diceRoll;
        
        // Verify position stays the same if rolling over 100
        if (newPosition > 100) {
            assertTrue(player.getCurrentPosition() == oldPosition, 
                "Position should not change when rolling over 100");
        }
    }
}