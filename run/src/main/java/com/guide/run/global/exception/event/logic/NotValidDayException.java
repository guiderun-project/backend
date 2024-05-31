package com.guide.run.global.exception.event.logic;

public class NotValidDayException extends RuntimeException {
    public NotValidDayException(String message){
        super(message);
    }
    public NotValidDayException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidDayException() {}
}
