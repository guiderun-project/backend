package com.guide.run.event.service;

import com.guide.run.event.entity.EventAdditionalOption;
import com.guide.run.event.entity.EventAdditionalQuestion;
import com.guide.run.event.entity.dto.request.EventApplyRequest;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.repository.EventAdditionalAnswerRepository;
import com.guide.run.event.entity.repository.EventAdditionalOptionRepository;
import com.guide.run.event.entity.repository.EventAdditionalQuestionRepository;
import com.guide.run.event.entity.type.AdditionalQuestionType;
import com.guide.run.global.exception.event.logic.EventValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventAdditionalInfoServiceTest {

    @Mock
    private EventAdditionalQuestionRepository questionRepository;

    @Mock
    private EventAdditionalOptionRepository optionRepository;

    @Mock
    private EventAdditionalAnswerRepository answerRepository;

    @InjectMocks
    private EventAdditionalInfoService eventAdditionalInfoService;

    @Test
    @DisplayName("추가질문 저장 시 요청 순서대로 질문과 SELECT 옵션을 저장한다")
    void replaceQuestionsSavesQuestionsAndOptionsInDisplayOrder() {
        when(questionRepository.save(any(EventAdditionalQuestion.class)))
                .thenAnswer(invocation -> {
                    EventAdditionalQuestion question = invocation.getArgument(0);
                    return EventAdditionalQuestion.builder()
                            .id(question.getType() == AdditionalQuestionType.TEXT ? 10L : 20L)
                            .eventId(question.getEventId())
                            .type(question.getType())
                            .title(question.getTitle())
                            .displayOrder(question.getDisplayOrder())
                            .required(question.isRequired())
                            .build();
                });

        eventAdditionalInfoService.replaceQuestions(1L, List.of(
                new EventCreateRequest.AdditionalQuestionRequest(
                        AdditionalQuestionType.TEXT,
                        "하고 싶은 말",
                        null
                ),
                new EventCreateRequest.AdditionalQuestionRequest(
                        AdditionalQuestionType.SELECT,
                        "티셔츠 사이즈",
                        List.of("S", "M")
                )
        ));

        ArgumentCaptor<EventAdditionalQuestion> questionCaptor =
                ArgumentCaptor.forClass(EventAdditionalQuestion.class);
        verify(questionRepository, times(2)).save(questionCaptor.capture());

        assertThat(questionCaptor.getAllValues())
                .extracting(EventAdditionalQuestion::getDisplayOrder)
                .containsExactly(0, 1);

        ArgumentCaptor<EventAdditionalOption> optionCaptor =
                ArgumentCaptor.forClass(EventAdditionalOption.class);
        verify(optionRepository, times(2)).save(optionCaptor.capture());

        assertThat(optionCaptor.getAllValues())
                .extracting(EventAdditionalOption::getQuestionId, EventAdditionalOption::getLabel, EventAdditionalOption::getDisplayOrder)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(20L, "S", 0),
                        org.assertj.core.groups.Tuple.tuple(20L, "M", 1)
                );
    }

    @Test
    @DisplayName("TEXT 추가질문은 2개 이상 저장할 수 없다")
    void replaceQuestionsRejectsMultipleTextQuestions() {
        assertThatThrownBy(() -> eventAdditionalInfoService.replaceQuestions(1L, List.of(
                new EventCreateRequest.AdditionalQuestionRequest(AdditionalQuestionType.TEXT, "질문1", null),
                new EventCreateRequest.AdditionalQuestionRequest(AdditionalQuestionType.TEXT, "질문2", null)
        ))).isInstanceOf(EventValidationException.class)
                .hasMessageContaining("TEXT");
    }

    @Test
    @DisplayName("SELECT 추가질문은 옵션이 1개 이상이어야 한다")
    void replaceQuestionsRejectsSelectQuestionWithoutOptions() {
        assertThatThrownBy(() -> eventAdditionalInfoService.replaceQuestions(1L, List.of(
                new EventCreateRequest.AdditionalQuestionRequest(AdditionalQuestionType.SELECT, "사이즈", List.of())
        ))).isInstanceOf(EventValidationException.class)
                .hasMessageContaining("SELECT");
    }

    @Test
    @DisplayName("추가답변은 같은 이벤트의 추가질문에만 저장할 수 있다")
    void replaceAnswersRejectsQuestionFromAnotherEvent() {
        EventAdditionalQuestion otherEventQuestion = EventAdditionalQuestion.builder()
                .id(10L)
                .eventId(2L)
                .type(AdditionalQuestionType.TEXT)
                .title("다른 이벤트 질문")
                .build();
        when(questionRepository.findById(10L)).thenReturn(Optional.of(otherEventQuestion));

        assertThatThrownBy(() -> eventAdditionalInfoService.replaceAnswers(
                1L,
                55L,
                List.of(new EventApplyRequest.AdditionalAnswerRequest(
                        10L,
                        AdditionalQuestionType.TEXT,
                        "답변",
                        null
                ))
        )).isInstanceOf(EventValidationException.class)
                .hasMessage("해당 이벤트의 추가질문이 아닙니다.");
        verify(answerRepository).deleteAllByEventFormId(55L);
        verify(answerRepository, never()).save(any());
    }
}
