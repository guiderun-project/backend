package com.guide.run.global.exception.event.logic;

public class NotValidSortException extends RuntimeException{
    public NotValidSortException(String message){
        super(message);
    }
    public NotValidSortException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidSortException() {}
}
