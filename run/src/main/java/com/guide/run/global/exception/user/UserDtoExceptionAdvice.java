package com.guide.run.global.exception.user;

import com.guide.run.global.dto.response.FailResult;

import com.guide.run.global.exception.user.dto.*;
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
public class UserDtoExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //1001
    @ExceptionHandler(DuplicatedUserIdException.class)
    protected ResponseEntity<FailResult> DuplicatedUserIdException(DuplicatedUserIdException e){
        return ResponseEntity.status(409).body(responseService.getFailResult(
                getMessage("duplicatedUserId.code"),
                getMessage("duplicatedUserId.msg")));
    }
    //1002
    @ExceptionHandler(NotValidPasswordCombinationException.class)
    protected ResponseEntity<FailResult> NotValidPasswordCombinationException(NotValidPasswordCombinationException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidPasswordCombination.code"),
                getMessage("notValidPasswordCombination.msg")));
    }
    //1003
    @ExceptionHandler(NotValidPasswordLengthException.class)
    protected ResponseEntity<FailResult> NotValidPasswordLengthException(NotValidPasswordLengthException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidPasswordLength.code"),
                getMessage("notValidPasswordLength.msg")));
    }
    //1004
    @ExceptionHandler(BlankRequiredInfoException.class)
    protected ResponseEntity<FailResult> BlankRequiredInfoException(BlankRequiredInfoException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("blankRequiredInfo.code"),
                getMessage("blankRequiredInfo.msg")));
    }
    //1005
    @ExceptionHandler(NotValidPhoneNumberFormException.class)
    protected ResponseEntity<FailResult> NotValidPhoneNumberFormException(NotValidPhoneNumberFormException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidPhoneNumberForm.code"),
                getMessage("notValidPhoneNumberForm.msg")));
    }
    //1006
    @ExceptionHandler(NotAgreeTermException.class)
    protected ResponseEntity<FailResult> NotAgreeTermException(NotAgreeTermException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("NotAgreeTerm.code"),
                getMessage("NotAgreeTerm.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
