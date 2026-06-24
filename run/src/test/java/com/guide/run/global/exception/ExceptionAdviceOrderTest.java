package com.guide.run.global.exception;

import com.guide.run.global.exception.auth.AuthAuthorizeExceptionAdvice;
import com.guide.run.global.exception.event.EventResourceExceptionAdvice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionAdviceOrderTest {

    @Test
    @DisplayName("인증 예외 advice는 알 수 없는 예외 advice보다 먼저 실행된다")
    void authAdviceRunsBeforeUnknownAdvice() {
        assertThat(orderValue(AuthAuthorizeExceptionAdvice.class))
                .isLessThan(orderValue(UnknownExceptionAdvice.class));
    }

    @Test
    @DisplayName("이벤트 리소스 예외 advice는 알 수 없는 예외 advice보다 먼저 실행된다")
    void eventResourceAdviceRunsBeforeUnknownAdvice() {
        assertThat(orderValue(EventResourceExceptionAdvice.class))
                .isLessThan(orderValue(UnknownExceptionAdvice.class));
    }

    private static int orderValue(Class<?> adviceClass) {
        Order order = AnnotationUtils.findAnnotation(adviceClass, Order.class);
        return order == null ? Ordered.LOWEST_PRECEDENCE : order.value();
    }
}
