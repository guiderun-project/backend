package com.guide.run.global.exception.auth.authorize;

public class NotValidAccessTokenException extends RuntimeException{
    public NotValidAccessTokenException(String message){
        super(message);
    }
    public NotValidAccessTokenException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidAccessTokenException() {}
}
