package com.guide.run.global.webhook.tosspayments.service;

import com.guide.run.global.sms.cool.WebhookCoolSmsService;
import com.guide.run.global.webhook.tosspayments.dto.TossPaymentCustomerInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TossPaymentsWebhookAsyncServiceTest {

    @Mock
    private TossPaymentsOrderService tossPaymentsOrderService;

    @Mock
    private WebhookCoolSmsService webhookCoolSmsService;

    @InjectMocks
    private TossPaymentsWebhookAsyncService tossPaymentsWebhookAsyncService;

    @Test
    @DisplayName("고객 전화번호가 있으면 문자를 발송한다")
    void processPaymentCompletedSendsSms() {
        when(tossPaymentsOrderService.getPaymentByOrderId("order-123"))
                .thenReturn(new TossPaymentCustomerInfo(
                        "payment-key",
                        "order-123",
                        "2026 상반기 정회원 회비 납부",
                        30000L,
                        "홍길동",
                        "010-1234-5678"
                ));

        tossPaymentsWebhookAsyncService.processPaymentCompleted("payment-key", "order-123");

        verify(webhookCoolSmsService).sendPaymentCompletedMessage("010-1234-5678", "홍길동");
    }

    @Test
    @DisplayName("고객 전화번호가 없으면 문자 발송을 건너뛴다")
    void processPaymentCompletedSkipsWhenPhoneMissing() {
        when(tossPaymentsOrderService.getPaymentByOrderId("order-123"))
                .thenReturn(new TossPaymentCustomerInfo(
                        "payment-key",
                        "order-123",
                        "2026 상반기 정회원 회비 납부",
                        30000L,
                        "홍길동",
                        null
                ));

        tossPaymentsWebhookAsyncService.processPaymentCompleted("payment-key", "order-123");

        verifyNoInteractions(webhookCoolSmsService);
    }

    @Test
    @DisplayName("토스 조회 실패 시 예외를 외부로 전파하지 않는다")
    void processPaymentCompletedSwallowsLookupFailure() {
        when(tossPaymentsOrderService.getPaymentByOrderId("order-123"))
                .thenThrow(new RuntimeException("lookup failed"));

        assertThatCode(() -> tossPaymentsWebhookAsyncService.processPaymentCompleted("payment-key", "order-123"))
                .doesNotThrowAnyException();

        verifyNoInteractions(webhookCoolSmsService);
    }

    @Test
    @DisplayName("문자 발송 실패 시 예외를 외부로 전파하지 않는다")
    void processPaymentCompletedSwallowsSmsFailure() {
        when(tossPaymentsOrderService.getPaymentByOrderId("order-123"))
                .thenReturn(new TossPaymentCustomerInfo(
                        "payment-key",
                        "order-123",
                        "2026 상반기 정회원 회비 납부",
                        30000L,
                        "홍길동",
                        "010-1234-5678"
                ));
        doThrow(new RuntimeException("sms failed"))
                .when(webhookCoolSmsService)
                .sendPaymentCompletedMessage("010-1234-5678", "홍길동");

        assertThatCode(() -> tossPaymentsWebhookAsyncService.processPaymentCompleted("payment-key", "order-123"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("정회원 회비 상품이 아니면 문자 발송을 건너뛴다")
    void processPaymentCompletedSkipsWhenOrderIsNotMembershipFee() {
        when(tossPaymentsOrderService.getPaymentByOrderId("order-123"))
                .thenReturn(new TossPaymentCustomerInfo(
                        "payment-key",
                        "order-123",
                        "2026 상반기 10K PB 단축반 참가비",
                        30000L,
                        "홍길동",
                        "010-1234-5678"
                ));

        tossPaymentsWebhookAsyncService.processPaymentCompleted("payment-key", "order-123");

        verifyNoInteractions(webhookCoolSmsService);
    }
}
