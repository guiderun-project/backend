package com.guide.run.global.exception.user;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserResourceExceptionAdviceTest {
    @Autowired
    UserResourceExceptionAdvice userResourceExceptionAdvice;
    //  code: "1300"
    //  msg: "존재하지 않는 사용자 입니다."
    @Test
    @DisplayName("존재하지 않는 사용자 에러코드 테스트")
    void notValidAccessTokenException() {
        ResponseEntity<FailResult> failResultResponseEntity = userResourceExceptionAdvice.NotExistUserException(new NotExistUserException());
        Assertions.assertThat(failResultResponseEntity.getBody().getCode()).isEqualTo("1300");
        Assertions.assertThat(failResultResponseEntity.getBody().getMsg()).isEqualTo("존재하지 않는 사용자 입니다.");
    }
}