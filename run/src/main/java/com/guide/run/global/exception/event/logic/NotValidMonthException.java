package com.guide.run.global.exception.event.logic;

public class NotValidMonthException extends RuntimeException{
    public NotValidMonthException(String message){
        super(message);
    }
    public NotValidMonthException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidMonthException() {}
}
