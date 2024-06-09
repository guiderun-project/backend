package com.guide.run.global.exception.event.logic;

public class ExistFormException extends RuntimeException{
    public ExistFormException(String message){
        super(message);
    }
    public ExistFormException(String message, Throwable cause){
        super(message,cause);
    }
    public ExistFormException() {}
}
