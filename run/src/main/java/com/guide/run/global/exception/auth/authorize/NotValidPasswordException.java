package com.guide.run.global.exception.auth.authorize;

public class NotValidPasswordException extends RuntimeException{
    public NotValidPasswordException(String message){
        super(message);
    }
    public NotValidPasswordException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidPasswordException() {}
}
