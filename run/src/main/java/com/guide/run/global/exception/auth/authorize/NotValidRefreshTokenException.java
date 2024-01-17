package com.guide.run.global.exception.auth.authorize;

public class NotValidRefreshTokenException extends RuntimeException{
    public NotValidRefreshTokenException(String message){
        super(message);
    }
    public NotValidRefreshTokenException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidRefreshTokenException() {}
}
