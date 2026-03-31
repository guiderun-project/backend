package com.guide.run.global.sms.cool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebhookCoolSmsServiceTest {

    private final WebhookCoolSmsService webhookCoolSmsService = new WebhookCoolSmsService();

    @Test
    @DisplayName("이름이 있으면 이름으로 시작하는 문자 문구를 만든다")
    void buildMessageTextUsesCustomerName() {
        String message = webhookCoolSmsService.buildMessageText("홍길동");

        assertThat(message).startsWith("홍길동님 회비 납부를 완료해주셔서 감사합니다.");
        assertThat(message).contains("https://open.kakao.com/o/gLM7V1ni");
    }

    @Test
    @DisplayName("이름이 없으면 기본 안내 문구로 시작한다")
    void buildMessageTextUsesDefaultGreeting() {
        String message = webhookCoolSmsService.buildMessageText(null);

        assertThat(message).startsWith("안녕하세요, 가이드런프로젝트입니다.");
        assertThat(message).contains("비밀번호[ 0401 ]");
    }

    @Test
    @DisplayName("전화번호는 숫자만 남기도록 정규화한다")
    void normalizePhoneNumberRemovesNonDigits() {
        String normalized = webhookCoolSmsService.normalizePhoneNumber("010-1234-5678");

        assertThat(normalized).isEqualTo("01012345678");
    }
}
