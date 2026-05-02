package com.snakeandladder.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameLogger {
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static final String LOG_PREFIX = "[SNAKE_AND_LADDER]";
    
    private GameLogger() {
        // Private constructor to prevent instantiation
    }
    
    public static void info(String message) {
        System.out.println(String.format("%s [INFO] %s - %s", 
            LOG_PREFIX, getCurrentTime(), message));
    }
    
    public static void debug(String message) {
        System.out.println(String.format("%s [DEBUG] %s - %s", 
            LOG_PREFIX, getCurrentTime(), message));
    }
    
    public static void error(String message) {
        System.err.println(String.format("%s [ERROR] %s - %s", 
            LOG_PREFIX, getCurrentTime(), message));
    }
    
    public static void warn(String message) {
        System.out.println(String.format("%s [WARN] %s - %s", 
            LOG_PREFIX, getCurrentTime(), message));
    }
    
    public static void gameEvent(String event) {
        System.out.println(String.format("%s [GAME] %s", LOG_PREFIX, event));
    }
    
    private static String getCurrentTime() {
        return LocalDateTime.now().format(formatter);
    }
}