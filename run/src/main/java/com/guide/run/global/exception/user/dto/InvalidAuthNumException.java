package com.guide.run.global.exception.user.dto;

public class InvalidAuthNumException extends RuntimeException{
    public InvalidAuthNumException(String message){
        super(message);
    }
    public InvalidAuthNumException(String message, Throwable cause){
        super(message,cause);
    }
    public InvalidAuthNumException() {}
}
