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

//니중에 해당 컨트롤러에 실패 테스트 코드 작성 후 지워야 할 듯하다
//상태코드도 비교하는 코드가 필요할 것 같다 404인지 400인지 ..
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