package com.guide.run.global.exception.user.dto;

public class DuplicatedUserIdException extends RuntimeException{
    public DuplicatedUserIdException(String message){
        super(message);
    }
    public DuplicatedUserIdException(String message, Throwable cause){
        super(message,cause);
    }
    public DuplicatedUserIdException() {}
}
