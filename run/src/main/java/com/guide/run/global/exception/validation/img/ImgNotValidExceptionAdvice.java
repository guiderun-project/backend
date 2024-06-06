package com.guide.run.global.exception.validation.img;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.user.authorize.NotApprovedUserException;
import com.guide.run.global.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ImgNotValidExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;
    //7001
    @ExceptionHandler(ImgNotValidException.class)
    protected ResponseEntity<FailResult> NotApprovedUserException(ImgNotValidException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidImgFormat.code"),
                getMessage("notValidImgFormat.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
