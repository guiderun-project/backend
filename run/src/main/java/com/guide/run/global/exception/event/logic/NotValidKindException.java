package com.guide.run.global.exception.event.logic;

public class NotValidKindException extends RuntimeException{
    public NotValidKindException(String message){
        super(message);
    }
    public NotValidKindException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidKindException() {}
}
