package com.guide.run.global.exception.user.authorize;

public class NotValidTmpTokenException extends RuntimeException{
    public NotValidTmpTokenException(String message){
        super(message);
    }
    public NotValidTmpTokenException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidTmpTokenException() {}
}
