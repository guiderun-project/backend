package com.guide.run.global.exception.event.dto;

public class NotValidEventStartException extends RuntimeException{
    public NotValidEventStartException(String message){
        super(message);
    }
    public NotValidEventStartException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidEventStartException() {}
}
