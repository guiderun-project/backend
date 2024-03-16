package com.guide.run.global.exception.user.dto;

public class NotValidPasswordLengthException extends RuntimeException{
    public NotValidPasswordLengthException(String message){
        super(message);
    }
    public NotValidPasswordLengthException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidPasswordLengthException() {}
}
