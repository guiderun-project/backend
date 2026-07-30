package com.guide.run.event.controller;

import com.guide.run.event.entity.Comment;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.request.EventCommentCreateRequest;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class EventTextLengthValidationTest {

    private final Validator validator = createValidator();

    @Test
    @DisplayName("이벤트 생성/수정 요청 content는 1000자 초과를 검증 실패로 처리한다")
    void eventContentRejectsMoreThan1000Characters() throws Exception {
        Field field = EventCreateRequest.class.getDeclaredField("content");
        Size size = field.getAnnotation(Size.class);

        assertThat(size).isNotNull();
        assertThat(size.max()).isEqualTo(1000);
        assertThat(validator.validate(eventRequestWithContent("a".repeat(1000)))).isEmpty();
        assertThat(validator.validate(eventRequestWithContent("a".repeat(1001))))
                .anySatisfy(violation -> assertThat(violation.getPropertyPath().toString()).isEqualTo("content"));
    }

    @Test
    @DisplayName("댓글 생성/수정 요청 content는 500자 초과를 검증 실패로 처리한다")
    void commentContentRejectsMoreThan500Characters() throws Exception {
        Field field = EventCommentCreateRequest.class.getDeclaredField("content");
        Size size = field.getAnnotation(Size.class);

        assertThat(size).isNotNull();
        assertThat(size.max()).isEqualTo(500);
        assertThat(validator.validate(new EventCommentCreateRequest("a".repeat(500)))).isEmpty();
        assertThat(validator.validate(new EventCommentCreateRequest("a".repeat(501))))
                .anySatisfy(violation -> assertThat(violation.getPropertyPath().toString()).isEqualTo("content"));
    }

    @Test
    @DisplayName("이벤트와 댓글 저장 컬럼 길이는 요청 제한과 동일하게 명시한다")
    void entityColumnsDeclareTextLengthLimits() throws Exception {
        Column eventContentColumn = Event.class.getDeclaredField("content").getAnnotation(Column.class);
        Column commentColumn = Comment.class.getDeclaredField("comment").getAnnotation(Column.class);

        assertThat(eventContentColumn).isNotNull();
        assertThat(eventContentColumn.length()).isEqualTo(1000);
        assertThat(commentColumn).isNotNull();
        assertThat(commentColumn.length()).isEqualTo(500);
    }

    @Test
    @DisplayName("이벤트와 댓글 컨트롤러는 요청 DTO 검증을 활성화한다")
    void controllersValidateLengthLimitedRequests() {
        assertThat(hasValidRequestParameter(EventController.class, "eventCreate", EventCreateRequest.class)).isTrue();
        assertThat(hasValidRequestParameter(EventController.class, "eventUpdate", EventCreateRequest.class)).isTrue();
        assertThat(hasValidRequestParameter(EventCommentController.class, "createComment", EventCommentCreateRequest.class)).isTrue();
        assertThat(hasValidRequestParameter(EventCommentController.class, "patchComment", EventCommentCreateRequest.class)).isTrue();
    }

    private static Validator createValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    private EventCreateRequest eventRequestWithContent(String content) {
        return new EventCreateRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                null,
                content,
                null,
                null,
                null,
                null,
                null
        );
    }

    private boolean hasValidRequestParameter(Class<?> controllerClass, String methodName, Class<?> requestType) {
        return Arrays.stream(controllerClass.getDeclaredMethods())
                .filter(method -> method.getName().equals(methodName))
                .map(Method::getParameters)
                .flatMap(Arrays::stream)
                .filter(parameter -> parameter.getType().equals(requestType))
                .anyMatch(this::hasValidAnnotation);
    }

    private boolean hasValidAnnotation(Parameter parameter) {
        return parameter.isAnnotationPresent(Valid.class);
    }
}
