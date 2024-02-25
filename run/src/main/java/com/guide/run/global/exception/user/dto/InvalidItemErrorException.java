package com.guide.run.global.exception.user.dto;

public class InvalidItemErrorException extends RuntimeException{
    public InvalidItemErrorException(String message){
        super(message);
    }
    public InvalidItemErrorException(String message, Throwable cause){
        super(message,cause);
    }
    public InvalidItemErrorException() {}
}
