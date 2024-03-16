package com.guide.run.global.exception.user.dto;

public class NotValidPasswordCombinationException extends RuntimeException{
    public NotValidPasswordCombinationException(String message){
        super(message);
    }
    public NotValidPasswordCombinationException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidPasswordCombinationException() {}
}
