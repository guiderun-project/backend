package com.guide.run.global.exception.user;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.user.resource.NotExistUserException;
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
public class UserResourceException {
    private final MessageSource messageSource;
    private final ResponseService responseService;
    //1300
    @ExceptionHandler(NotExistUserException.class)
    protected ResponseEntity<FailResult> NotValidAccessTokenException(NotExistUserException e){
        return ResponseEntity.status(404).body(responseService.getFailResult(
                getMessage("notExistUser.code"),
                getMessage("notExistUser.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
