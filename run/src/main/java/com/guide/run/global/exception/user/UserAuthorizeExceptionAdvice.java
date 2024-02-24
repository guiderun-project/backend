package com.guide.run.global.exception.user;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.user.authorize.ExistUserException;
import com.guide.run.global.exception.user.authorize.NotApprovedUserException;
import com.guide.run.global.exception.user.authorize.NotAuthorizationException;
import com.guide.run.global.exception.user.authorize.UnauthorizedUserException;
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
public class UserAuthorizeExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //1100
    @ExceptionHandler(NotApprovedUserException.class)
    protected ResponseEntity<FailResult> NotApprovedUserException(NotApprovedUserException e){
        return ResponseEntity.status(403).body(responseService.getFailResult(
                getMessage("notApprovedUser.code"),
                getMessage("notApprovedUser.msg")));
    }
    //1101
    @ExceptionHandler(NotAuthorizationException.class)
    protected ResponseEntity<FailResult> NotAuthorizationException(NotAuthorizationException e){
        return ResponseEntity.status(401).body(responseService.getFailResult(
                getMessage("notAuthorization.code"),
                getMessage("notAuthorization.msg")));
    }

    //1102
    @ExceptionHandler(UnauthorizedUserException.class)
    protected ResponseEntity<FailResult> UnauthorizedUserException(UnauthorizedUserException e){
        return ResponseEntity.status(403).body(responseService.getFailResult(
                getMessage("unauthorizedUser.code"),
                getMessage("unauthorizedUser.msg")));
    }

    //1103
    @ExceptionHandler(ExistUserException.class)
    protected ResponseEntity<FailResult> UnauthorizedUserException(ExistUserException e){
        return ResponseEntity.status(403).body(responseService.getFailResult(
                getMessage("existUser.code"),
                getMessage("existUser.msg")));
    }
    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
