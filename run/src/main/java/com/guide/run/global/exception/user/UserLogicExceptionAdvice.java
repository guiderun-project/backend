package com.guide.run.global.exception.user;


import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.user.dto.NotExistPhoneNumException;
import com.guide.run.global.exception.user.logic.InvalidAccountIdAndPhoneException;
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
public class UserLogicExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //1200
    @ExceptionHandler(InvalidAccountIdAndPhoneException.class)
    protected ResponseEntity<FailResult> InvalidAccountIdAndPhoneException(InvalidAccountIdAndPhoneException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("InvalidAccountIdAndPhone.code"),
                getMessage("InvalidAccountIdAndPhone.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
