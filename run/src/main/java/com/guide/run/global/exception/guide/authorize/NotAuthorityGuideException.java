package com.guide.run.global.exception.guide.authorize;

public class NotAuthorityGuideException extends RuntimeException{
    public NotAuthorityGuideException(String message){
        super(message);
    }
    public NotAuthorityGuideException(String message, Throwable cause){
        super(message,cause);
    }
    public NotAuthorityGuideException() {}
}
