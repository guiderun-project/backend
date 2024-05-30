package com.guide.run.global.exception.auth;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.auth.authorize.*;
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
public class AuthAuthorizeExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //0001
    @ExceptionHandler(NotValidAccountIdException.class)
    protected ResponseEntity<FailResult> NotValidAccountIdException(NotValidAccountIdException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidAccountId.code"),
                getMessage("notValidAccountId.msg")));
    }

    //0002
    @ExceptionHandler(NotValidPasswordException.class)
    protected ResponseEntity<FailResult> NotValidPasswordException(NotValidPasswordException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidPassword.code"),
                getMessage("notValidPassword.msg")));
    }

    //0100
    @ExceptionHandler(NotValidAccessTokenException.class)
    protected ResponseEntity<FailResult> NotValidAccessTokenException(NotValidAccessTokenException e){
        return ResponseEntity.status(401).body(responseService.getFailResult(
                getMessage("notValidAccessToken.code"),
                getMessage("notValidAccessToken.msg")));
    }

    //0101
    @ExceptionHandler(NotExistAuthorizationException.class)
    protected ResponseEntity<FailResult> NotExistAuthorizationException(NotExistAuthorizationException e){
        return ResponseEntity.status(404).body(responseService.getFailResult(
                getMessage("notExistAuthorization.code"),
                getMessage("notExistAuthorization.msg")));
    }

    //0104
    @ExceptionHandler(NotValidRefreshTokenException.class)
    protected ResponseEntity<FailResult> NotValidRefreshTokenException(NotValidRefreshTokenException e){
        return ResponseEntity.status(401).body(responseService.getFailResult(
                getMessage("notValidRefreshToken.code"),
                getMessage("notValidRefreshToken.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
