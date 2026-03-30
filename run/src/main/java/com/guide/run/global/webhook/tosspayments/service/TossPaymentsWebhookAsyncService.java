package com.guide.run.global.webhook.tosspayments.service;

import com.guide.run.global.sms.cool.WebhookCoolSmsService;
import com.guide.run.global.webhook.tosspayments.dto.TossPaymentCustomerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentsWebhookAsyncService {

    private static final List<String> SMS_ALLOWED_ORDER_NAME_KEYWORDS = List.of(
            "정회원 회비 납부",
            "테스트"
    );

    private final TossPaymentsOrderService tossPaymentsOrderService;
    private final WebhookCoolSmsService webhookCoolSmsService;

    @Async("tossWebhookTaskExecutor")
    public void processPaymentCompleted(String paymentKey, String orderId) {
        try {
            TossPaymentCustomerInfo paymentInfo = tossPaymentsOrderService.getPaymentByOrderId(orderId);
            String resolvedPaymentKey = StringUtils.hasText(paymentInfo.paymentKey())
                    ? paymentInfo.paymentKey() : paymentKey;

            if (!isMembershipFeePayment(paymentInfo.orderName())) {
                log.info("Skip webhook sms because order is not membership fee. paymentKey={}, orderId={}, orderName={}",
                        resolvedPaymentKey, orderId, paymentInfo.orderName());
                return;
            }

            if (!StringUtils.hasText(paymentInfo.customerPhone())) {
                log.warn("Customer phone number not found. paymentKey={}, orderId={}", resolvedPaymentKey, orderId);
                return;
            }

            webhookCoolSmsService.sendPaymentCompletedMessage(
                    paymentInfo.customerPhone(),
                    paymentInfo.customerName()
            );
        } catch (Exception e) {
            log.error("Failed to process tosspayments webhook. paymentKey={}, orderId={}", paymentKey, orderId, e);
        }
    }

    private boolean isMembershipFeePayment(String orderName) {
        return StringUtils.hasText(orderName)
                && SMS_ALLOWED_ORDER_NAME_KEYWORDS.stream().anyMatch(orderName::contains);
    }
}
