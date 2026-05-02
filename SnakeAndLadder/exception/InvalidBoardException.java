package com.snakeandladder.model.exception;

public class InvalidBoardException extends Exception{

    public InvalidBoardException(String format) {
        super(format);
    }
}
