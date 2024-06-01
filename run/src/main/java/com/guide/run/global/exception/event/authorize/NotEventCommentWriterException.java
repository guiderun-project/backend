package com.guide.run.global.exception.event.authorize;

public class NotEventCommentWriterException extends RuntimeException{
    public NotEventCommentWriterException(String message){
        super(message);
    }
    public NotEventCommentWriterException(String message, Throwable cause){
        super(message,cause);
    }
    public NotEventCommentWriterException() {}
}
