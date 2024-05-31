package com.guide.run.global.exception.event.logic;

public class NotValidYearException extends RuntimeException{
    public NotValidYearException(String message){
        super(message);
    }
    public NotValidYearException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidYearException() {}
}
