package com.guide.run.global.exception;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.user.NotExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class UserExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;
    private final JwtProvider jwtProvider;

    @ExceptionHandler({NotExistUserException.class, UsernameNotFoundException.class})
    protected ResponseEntity<FailResult> NotValidAccessTokenException(HttpServletRequest request,NotExistUserException e){
        log.error(getMessage("notExistUser.msg"));
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
