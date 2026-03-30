package com.guide.run.global.webhook.tosspayments.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TossPaymentsWebhookRequest {
    private String eventType;
    private String createdAt;
    private WebhookData data;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class WebhookData {
        private String paymentKey;
        private String status;
        private String orderId;
    }
}
