package com.guide.run.global.webhook.tosspayments.dto;

public record TossPaymentCustomerInfo(
        String paymentKey,
        String orderId,
        String orderName,
        Long totalAmount,
        String customerName,
        String customerPhone
) {
}
