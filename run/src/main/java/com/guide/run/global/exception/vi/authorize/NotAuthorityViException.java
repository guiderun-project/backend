package com.guide.run.global.exception.vi.authorize;

public class NotAuthorityViException extends RuntimeException{
    public NotAuthorityViException(String message){
        super(message);
    }
    public NotAuthorityViException(String message, Throwable cause){
        super(message,cause);
    }
    public NotAuthorityViException() {}
}
