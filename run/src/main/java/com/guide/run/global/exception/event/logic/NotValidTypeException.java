package com.guide.run.global.exception.event.logic;

public class NotValidTypeException extends RuntimeException{
    public NotValidTypeException(String message){
        super(message);
    }
    public NotValidTypeException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidTypeException() {}
}
