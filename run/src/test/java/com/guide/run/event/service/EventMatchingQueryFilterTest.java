package com.guide.run.event.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class EventMatchingQueryFilterTest {

    @Test
    @DisplayName("매칭 대기 조회 QueryDSL은 APPLIED 신청서만 조인한다")
    void matchingWaitingQueryFiltersAppliedEventForms() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/guide/run/partner/entity/matching/repository/UnMatchingRepositoryImpl.java"
        ));

        assertThat(source).contains("eventForm.status.eq(EventFormStatus.APPLIED)");
    }

    @Test
    @DisplayName("매칭 대기 조회 QueryDSL은 사용자 기본 그룹(recordDegree)을 조회한다")
    void matchingWaitingQuerySelectsUserRecordDegree() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/guide/run/partner/entity/matching/repository/UnMatchingRepositoryImpl.java"
        ));

        assertThat(source).contains("user.recordDegree.as(\"recordDegree\")");
    }

    @Test
    @DisplayName("매칭 대기 조회 QueryDSL은 추가질문 답변 조회용 신청서 ID를 조회한다")
    void matchingWaitingQuerySelectsEventFormId() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/guide/run/partner/entity/matching/repository/UnMatchingRepositoryImpl.java"
        ));

        assertThat(source).contains("eventForm.id.as(\"eventFormId\")");
    }

    @Test
    @DisplayName("매칭 완료 조회 QueryDSL은 APPLIED 신청서만 조인한다")
    void matchingCompletedQueryFiltersAppliedEventForms() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/guide/run/partner/entity/matching/repository/MatchingRepositoryImpl.java"
        ));

        assertThat(source).contains("viForm.status.eq(EventFormStatus.APPLIED)");
        assertThat(source).contains("guideForm.status.eq(EventFormStatus.APPLIED)");
    }
}
