package com.guide.run.global.exception;

import com.guide.run.global.exception.admin.AdminAuthorizeExceptionAdvice;
import com.guide.run.global.exception.auth.AuthAuthorizeExceptionAdvice;
import com.guide.run.global.exception.coach.CoachAuthorizeExceptionAdvice;
import com.guide.run.global.exception.event.EventAuthorizeExceptionAdvice;
import com.guide.run.global.exception.event.EventDtoExceptionAdvice;
import com.guide.run.global.exception.event.EventLogicExceptionAdvice;
import com.guide.run.global.exception.event.EventResourceExceptionAdvice;
import com.guide.run.global.exception.guide.GuideAuthorizeExceptionAdvice;
import com.guide.run.global.exception.validation.ValidationExceptionAdvice;
import com.guide.run.global.exception.user.UserAuthorizeExceptionAdvice;
import com.guide.run.global.exception.user.UserDtoExceptionAdvice;
import com.guide.run.global.exception.user.UserLogicExceptionAdvice;
import com.guide.run.global.exception.user.UserResourceExceptionAdvice;
import com.guide.run.global.exception.vi.ViAuthorizeExceptionAdvice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionAdviceOrderTest {

    @Test
    @DisplayName("구체 예외 advice는 알 수 없는 예외 advice보다 먼저 실행된다")
    void concreteAdviceRunsBeforeUnknownAdvice() {
        List<Class<?>> concreteAdviceClasses = List.of(
                GlobalExceptionAdvice.class,
                AuthAuthorizeExceptionAdvice.class,
                EventResourceExceptionAdvice.class,
                EventLogicExceptionAdvice.class,
                EventAuthorizeExceptionAdvice.class,
                EventDtoExceptionAdvice.class,
                AdminAuthorizeExceptionAdvice.class,
                CoachAuthorizeExceptionAdvice.class,
                GuideAuthorizeExceptionAdvice.class,
                ViAuthorizeExceptionAdvice.class,
                ValidationExceptionAdvice.class,
                UserResourceExceptionAdvice.class,
                UserAuthorizeExceptionAdvice.class,
                UserDtoExceptionAdvice.class,
                UserLogicExceptionAdvice.class
        );

        concreteAdviceClasses.forEach(adviceClass -> assertThat(orderValue(adviceClass))
                .as(adviceClass.getSimpleName())
                .isLessThan(orderValue(UnknownExceptionAdvice.class)));
    }

    private static int orderValue(Class<?> adviceClass) {
        Order order = AnnotationUtils.findAnnotation(adviceClass, Order.class);
        return order == null ? Ordered.LOWEST_PRECEDENCE : order.value();
    }
}
