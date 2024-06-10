package com.guide.run.global.exception.event.logic;

public class NotValidDurationException extends RuntimeException{
    public NotValidDurationException(String message){
        super(message);
    }
    public NotValidDurationException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidDurationException() {}
}
