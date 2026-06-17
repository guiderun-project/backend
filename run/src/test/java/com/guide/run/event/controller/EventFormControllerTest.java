package com.guide.run.event.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class EventFormControllerTest {

    @Test
    @DisplayName("특정 사용자 신청서 조회 레거시 API는 노출하지 않는다")
    void legacyGetFormEndpointIsRemoved() {
        boolean hasLegacyMapping = Arrays.stream(EventFormController.class.getDeclaredMethods())
                .map(method -> method.getAnnotation(GetMapping.class))
                .filter(mapping -> mapping != null)
                .map(GetMapping::value)
                .flatMap(Arrays::stream)
                .anyMatch("/{eventId}/form/{userId}"::equals);

        assertThat(hasLegacyMapping).isFalse();
    }
}
