package com.guide.run.event.controller;

import com.guide.run.user.controller.LoginInfoController;
import com.guide.run.user.controller.SignController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

import static org.assertj.core.api.Assertions.assertThat;

class EventCorsControllerTest {

    @Test
    @DisplayName("일반 API 컨트롤러는 전역 CORS 설정에 의존한다")
    void apiControllersUseGlobalCorsConfiguration() {
        assertThat(corsAnnotation(EventGetController.class)).isNull();
        assertThat(corsAnnotation(EventSearchController.class)).isNull();
        assertThat(corsAnnotation(EventMatchingController.class)).isNull();
        assertThat(corsAnnotation(SignController.class)).isNull();
        assertThat(corsAnnotation(LoginInfoController.class)).isNull();
    }

    @Test
    @DisplayName("이벤트 테스트 컨트롤러는 전역 CORS 일원화 대상에서 제외한다")
    void eventTestControllerKeepsExplicitCorsConfiguration() {
        assertThat(corsAnnotation(EventTestController.class)).isNotNull();
    }

    private static CrossOrigin corsAnnotation(Class<?> controllerClass) {
        return AnnotationUtils.findAnnotation(controllerClass, CrossOrigin.class);
    }
}
