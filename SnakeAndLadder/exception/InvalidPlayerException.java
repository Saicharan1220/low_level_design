package com.snakeandladder.model.exception;

public class InvalidPlayerException extends Exception{
    public InvalidPlayerException(String format) {
        super(format);
    }
}
