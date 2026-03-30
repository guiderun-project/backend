package com.guide.run.global.webhook.tosspayments.controller;

import com.guide.run.global.webhook.tosspayments.dto.TossPaymentsWebhookRequest;
import com.guide.run.global.webhook.tosspayments.service.TossPaymentsWebhookAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TossPaymentsWebhookController {

    private static final String PAYMENT_STATUS_CHANGED = "PAYMENT_STATUS_CHANGED";
    private static final String DONE = "DONE";

    private final TossPaymentsWebhookAsyncService tossPaymentsWebhookAsyncService;

    @PostMapping("/webhook/tosspayments")
    public ResponseEntity<Void> receiveWebhook(@RequestBody(required = false) TossPaymentsWebhookRequest request) {
        log.info("Received tosspayments webhook payload: {}", request);

        if (shouldProcess(request)) {
            try {
                tossPaymentsWebhookAsyncService.processPaymentCompleted(
                        request.getData().getPaymentKey(),
                        request.getData().getOrderId()
                );
            } catch (Exception e) {
                log.error("Failed to dispatch tosspayments webhook async task. orderId={}",
                        request.getData().getOrderId(), e);
            }
        }

        return ResponseEntity.ok().build();
    }

    private boolean shouldProcess(TossPaymentsWebhookRequest request) {
        return request != null
                && PAYMENT_STATUS_CHANGED.equals(request.getEventType())
                && request.getData() != null
                && DONE.equals(request.getData().getStatus())
                && StringUtils.hasText(request.getData().getOrderId());
    }
}
