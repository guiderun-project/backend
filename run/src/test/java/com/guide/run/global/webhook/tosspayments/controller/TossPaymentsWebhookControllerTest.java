package com.guide.run.global.webhook.tosspayments.controller;

import com.guide.run.global.webhook.tosspayments.service.TossPaymentsWebhookAsyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TossPaymentsWebhookControllerTest {

    @Mock
    private TossPaymentsWebhookAsyncService tossPaymentsWebhookAsyncService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        TossPaymentsWebhookController controller =
                new TossPaymentsWebhookController(tossPaymentsWebhookAsyncService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("PAYMENT_STATUS_CHANGED DONE 웹훅은 비동기 처리로 넘긴다")
    void receiveWebhookDispatchesAsyncTask() throws Exception {
        mockMvc.perform(post("/webhook/tosspayments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventType": "PAYMENT_STATUS_CHANGED",
                                  "createdAt": "2022-05-12T00:00:00.000",
                                  "data": {
                                    "paymentKey": "payment-key",
                                    "status": "DONE",
                                    "orderId": "order-123"
                                  }
                                }
                                """))
                .andExpect(status().isOk());

        verify(tossPaymentsWebhookAsyncService).processPaymentCompleted("payment-key", "order-123");
    }

    @Test
    @DisplayName("처리 대상이 아닌 웹훅도 200을 반환하고 무시한다")
    void receiveWebhookIgnoresNonTargetEvent() throws Exception {
        mockMvc.perform(post("/webhook/tosspayments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventType": "PAYMENT_CANCELED",
                                  "createdAt": "2022-05-12T00:00:00.000",
                                  "data": {
                                    "paymentKey": "payment-key",
                                    "status": "DONE",
                                    "orderId": "order-123"
                                  }
                                }
                                """))
                .andExpect(status().isOk());

        verifyNoInteractions(tossPaymentsWebhookAsyncService);
    }

    @Test
    @DisplayName("orderId가 없으면 200을 반환하고 무시한다")
    void receiveWebhookReturnsOkWhenOrderIdMissing() throws Exception {
        mockMvc.perform(post("/webhook/tosspayments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventType": "PAYMENT_STATUS_CHANGED",
                                  "createdAt": "2022-05-12T00:00:00.000",
                                  "data": {
                                    "paymentKey": "payment-key",
                                    "status": "DONE",
                                    "orderId": ""
                                  }
                                }
                                """))
                .andExpect(status().isOk());

        verifyNoInteractions(tossPaymentsWebhookAsyncService);
    }

    @Test
    @DisplayName("비동기 작업 디스패치가 실패해도 200을 반환한다")
    void receiveWebhookReturnsOkWhenAsyncDispatchFails() throws Exception {
        doThrow(new TaskRejectedException("queue is full"))
                .when(tossPaymentsWebhookAsyncService)
                .processPaymentCompleted("payment-key", "order-123");

        mockMvc.perform(post("/webhook/tosspayments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventType": "PAYMENT_STATUS_CHANGED",
                                  "createdAt": "2022-05-12T00:00:00.000",
                                  "data": {
                                    "paymentKey": "payment-key",
                                    "status": "DONE",
                                    "orderId": "order-123"
                                  }
                                }
                                """))
                .andExpect(status().isOk());
    }
}
