package com.guide.run.global.exception;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.user.authorize.NotAuthorizationException;
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
public class UnknownExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //0000
    @ExceptionHandler(UnknownException.class)
    protected ResponseEntity<FailResult> UnknownException(UnknownException e){
        return ResponseEntity.status(500).body(responseService.getFailResult(
                getMessage("unknown.code"),
                getMessage("unknown.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
