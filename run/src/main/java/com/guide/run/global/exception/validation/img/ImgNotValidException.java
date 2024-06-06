package com.guide.run.global.exception.validation.img;

public class ImgNotValidException extends RuntimeException{
    public ImgNotValidException(String message){
        super(message);
    }
    public ImgNotValidException(String message, Throwable cause){
        super(message,cause);
    }
    public ImgNotValidException() {}
}
