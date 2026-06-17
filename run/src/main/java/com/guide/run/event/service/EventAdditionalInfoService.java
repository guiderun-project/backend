package com.guide.run.event.service;

import com.guide.run.event.entity.EventAdditionalAnswer;
import com.guide.run.event.entity.EventAdditionalOption;
import com.guide.run.event.entity.EventAdditionalQuestion;
import com.guide.run.event.entity.dto.request.EventApplyRequest;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.response.EventDetailResponse;
import com.guide.run.event.entity.dto.response.form.MyEventApplyGetResponse;
import com.guide.run.event.entity.repository.EventAdditionalAnswerRepository;
import com.guide.run.event.entity.repository.EventAdditionalOptionRepository;
import com.guide.run.event.entity.repository.EventAdditionalQuestionRepository;
import com.guide.run.event.entity.type.AdditionalQuestionType;
import com.guide.run.global.exception.event.logic.EventValidationException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventAdditionalInfoService {
    private final EventAdditionalQuestionRepository questionRepository;
    private final EventAdditionalOptionRepository optionRepository;
    private final EventAdditionalAnswerRepository answerRepository;

    @Transactional
    public void replaceQuestions(Long eventId, List<EventCreateRequest.AdditionalQuestionRequest> requests) {
        if (requests == null) {
            return;
        }

        validateQuestions(requests);
        deleteAllForEvent(eventId);

        for (int questionIndex = 0; questionIndex < requests.size(); questionIndex++) {
            EventCreateRequest.AdditionalQuestionRequest request = requests.get(questionIndex);
            EventAdditionalQuestion savedQuestion = questionRepository.save(EventAdditionalQuestion.builder()
                    .eventId(eventId)
                    .type(request.getType())
                    .title(request.getTitle())
                    .displayOrder(questionIndex)
                    .required(false)
                    .build());

            if (request.getType() == AdditionalQuestionType.SELECT) {
                List<String> options = request.getOptions();
                for (int optionIndex = 0; optionIndex < options.size(); optionIndex++) {
                    optionRepository.save(EventAdditionalOption.builder()
                            .questionId(savedQuestion.getId())
                            .label(options.get(optionIndex))
                            .displayOrder(optionIndex)
                            .build());
                }
            }
        }
    }

    public List<EventDetailResponse.AdditionalQuestion> getQuestions(Long eventId) {
        List<EventAdditionalQuestion> questions = questionRepository.findAllByEventIdOrderByDisplayOrderAsc(eventId);
        if (questions.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> questionIds = questions.stream()
                .map(EventAdditionalQuestion::getId)
                .toList();
        Map<Long, List<EventAdditionalOption>> optionMap = optionRepository
                .findAllByQuestionIdInOrderByDisplayOrderAsc(questionIds)
                .stream()
                .collect(Collectors.groupingBy(EventAdditionalOption::getQuestionId));

        return questions.stream()
                .map(question -> EventDetailResponse.AdditionalQuestion.builder()
                        .questionId(question.getId())
                        .type(question.getType())
                        .question(question.getTitle())
                        .options(toDetailOptions(optionMap.getOrDefault(question.getId(), Collections.emptyList())))
                        .build())
                .toList();
    }

    @Transactional
    public void replaceAnswers(Long eventId, Long eventFormId, List<EventApplyRequest.AdditionalAnswerRequest> requests) {
        answerRepository.deleteAllByEventFormId(eventFormId);
        if (requests == null || requests.isEmpty()) {
            return;
        }

        validateAnswerQuestionIds(requests);
        for (EventApplyRequest.AdditionalAnswerRequest request : requests) {
            EventAdditionalQuestion question = questionRepository.findById(request.getQuestionId())
                    .orElseThrow(() -> new EventValidationException("존재하지 않는 추가질문입니다."));
            validateQuestionBelongsToEvent(eventId, question);
            validateAnswer(question, request);

            answerRepository.save(EventAdditionalAnswer.builder()
                    .eventFormId(eventFormId)
                    .questionId(request.getQuestionId())
                    .textAnswer(request.getAnswerText())
                    .optionId(request.getSelectedOptionId())
                    .build());
        }
    }

    private void validateAnswerQuestionIds(List<EventApplyRequest.AdditionalAnswerRequest> requests) {
        Set<Long> seenQuestionIds = new HashSet<>();
        for (EventApplyRequest.AdditionalAnswerRequest request : requests) {
            if (!seenQuestionIds.add(request.getQuestionId())) {
                throw new EventValidationException("동일한 추가질문에 대한 답변은 1개만 가능합니다.");
            }
        }
    }

    public List<MyEventApplyGetResponse.AdditionalAnswerDetail> getAnswerDetails(Long eventFormId) {
        List<EventAdditionalAnswer> answers = answerRepository.findAllByEventFormId(eventFormId);
        if (answers.isEmpty()) {
            return Collections.emptyList();
        }

        List<MyEventApplyGetResponse.AdditionalAnswerDetail> details = new ArrayList<>();
        for (EventAdditionalAnswer answer : answers) {
            EventAdditionalQuestion question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(NotExistEventException::new);
            List<EventAdditionalOption> options = optionRepository.findAllByQuestionIdOrderByDisplayOrderAsc(question.getId());
            Optional<EventAdditionalOption> selectedOption = options.stream()
                    .filter(option -> option.getId().equals(answer.getOptionId()))
                    .findFirst();

            details.add(MyEventApplyGetResponse.AdditionalAnswerDetail.builder()
                    .questionId(question.getId())
                    .type(question.getType())
                    .question(question.getTitle())
                    .answerText(answer.getTextAnswer())
                    .selectedOptionId(answer.getOptionId())
                    .selectedOptionValue(selectedOption.map(EventAdditionalOption::getLabel).orElse(null))
                    .options(toApplyOptions(options))
                    .build());
        }
        return details;
    }

    @Transactional
    public void deleteAllForEvent(Long eventId) {
        List<EventAdditionalQuestion> questions = questionRepository.findAllByEventIdOrderByDisplayOrderAsc(eventId);
        if (questions.isEmpty()) {
            return;
        }

        List<Long> questionIds = questions.stream()
                .map(EventAdditionalQuestion::getId)
                .toList();
        answerRepository.deleteAllByQuestionIdIn(questionIds);
        optionRepository.deleteAllByQuestionIdIn(questionIds);
        questionRepository.deleteAllByEventId(eventId);
    }

    private void validateQuestions(List<EventCreateRequest.AdditionalQuestionRequest> requests) {
        long textCount = requests.stream()
                .filter(request -> request.getType() == AdditionalQuestionType.TEXT)
                .count();
        if (textCount > 1) {
            throw new EventValidationException("TEXT 추가질문은 최대 1개까지 가능합니다.");
        }

        long selectCount = requests.stream()
                .filter(request -> request.getType() == AdditionalQuestionType.SELECT)
                .count();
        if (selectCount > 1) {
            throw new EventValidationException("SELECT 추가질문은 최대 1개까지 가능합니다.");
        }

        for (EventCreateRequest.AdditionalQuestionRequest request : requests) {
            if (request.getType() == null) {
                throw new EventValidationException("추가질문 타입은 필수입니다.");
            }
            if (request.getType() == AdditionalQuestionType.SELECT
                    && (request.getOptions() == null || request.getOptions().isEmpty())) {
                throw new EventValidationException("SELECT 추가질문은 옵션이 1개 이상이어야 합니다.");
            }
        }
    }

    private void validateQuestionBelongsToEvent(Long eventId, EventAdditionalQuestion question) {
        if (!eventId.equals(question.getEventId())) {
            throw new EventValidationException("해당 이벤트의 추가질문이 아닙니다.");
        }
    }

    private void validateAnswer(EventAdditionalQuestion question, EventApplyRequest.AdditionalAnswerRequest request) {
        if (question.getType() != request.getType()) {
            throw new EventValidationException("추가답변 타입이 질문 타입과 일치하지 않습니다.");
        }

        if (question.getType() == AdditionalQuestionType.SELECT) {
            List<EventAdditionalOption> options = optionRepository.findAllByQuestionIdOrderByDisplayOrderAsc(question.getId());
            boolean optionBelongsToQuestion = options.stream()
                    .anyMatch(option -> option.getId().equals(request.getSelectedOptionId()));
            if (!optionBelongsToQuestion) {
                throw new EventValidationException("선택한 옵션이 질문에 속하지 않습니다.");
            }
        }
    }

    private List<EventDetailResponse.Option> toDetailOptions(List<EventAdditionalOption> options) {
        return options.stream()
                .map(option -> EventDetailResponse.Option.builder()
                        .optionId(option.getId())
                        .value(option.getLabel())
                        .build())
                .toList();
    }

    private List<MyEventApplyGetResponse.Option> toApplyOptions(List<EventAdditionalOption> options) {
        return options.stream()
                .map(option -> MyEventApplyGetResponse.Option.builder()
                        .optionId(option.getId())
                        .value(option.getLabel())
                        .build())
                .toList();
    }
}
