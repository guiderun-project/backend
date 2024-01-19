package com.guide.run.global.exception.user.dto;

public class BlankRequiredInfoException extends RuntimeException {
    public BlankRequiredInfoException(String message){
        super(message);
    }
    public BlankRequiredInfoException(String message, Throwable cause){
        super(message,cause);
    }
    public BlankRequiredInfoException() {}
}
