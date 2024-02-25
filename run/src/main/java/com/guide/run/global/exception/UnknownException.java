package com.guide.run.global.exception;

public class UnknownException extends RuntimeException{
    public UnknownException(String message){
        super(message);
    }
    public UnknownException(String message, Throwable cause){
        super(message,cause);
    }
    public UnknownException() {}
}
