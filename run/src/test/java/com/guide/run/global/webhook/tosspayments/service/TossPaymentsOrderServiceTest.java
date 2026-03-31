package com.guide.run.global.webhook.tosspayments.service;

import com.guide.run.global.webhook.tosspayments.dto.TossPaymentCustomerInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TossPaymentsOrderServiceTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockRestServiceServer;
    private TossPaymentsOrderService tossPaymentsOrderService;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        tossPaymentsOrderService = new TossPaymentsOrderService(restTemplate);
        ReflectionTestUtils.setField(tossPaymentsOrderService, "secretKey", "test_secret_key");
    }

    @Test
    @DisplayName("orderId로 결제를 조회할 때 Basic 인증 헤더와 고객 정보를 파싱한다")
    void getPaymentByOrderIdParsesCustomerInfo() {
        String encoded = Base64.getEncoder()
                .encodeToString("test_secret_key:".getBytes(StandardCharsets.UTF_8));

        mockRestServiceServer.expect(requestTo("https://linkpay-openapi.tosspayments.com/api/v1/orders/order-123"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Basic " + encoded))
                .andRespond(withSuccess("""
                        {
                          "code": "SUCCESS",
                          "message": "",
                          "result": {
                            "paymentKey": "payment-key",
                            "orderId": "order-123",
                            "amount": 30000,
                            "customerName": "홍길동",
                            "customerPhoneNumber": "010-1234-5678",
                            "orderItems": [
                              {
                                "product": {
                                  "name": "2026 상반기 정회원 회비 납부"
                                }
                              }
                            ]
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        TossPaymentCustomerInfo payment = tossPaymentsOrderService.getPaymentByOrderId("order-123");

        assertThat(payment.paymentKey()).isEqualTo("payment-key");
        assertThat(payment.orderId()).isEqualTo("order-123");
        assertThat(payment.orderName()).isEqualTo("2026 상반기 정회원 회비 납부");
        assertThat(payment.totalAmount()).isEqualTo(30000L);
        assertThat(payment.customerName()).isEqualTo("홍길동");
        assertThat(payment.customerPhone()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("고객 전화번호가 없으면 null로 반환한다")
    void getPaymentByOrderIdReturnsNullPhoneWhenMissing() {
        mockRestServiceServer.expect(requestTo("https://linkpay-openapi.tosspayments.com/api/v1/orders/order-456"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "code": "SUCCESS",
                          "message": "",
                          "result": {
                            "paymentKey": "payment-key",
                            "orderId": "order-456",
                            "amount": 30000,
                            "customerName": "홍길동",
                            "orderItems": [
                              {
                                "product": {
                                  "name": "2026 상반기 정회원 회비 납부"
                                }
                              }
                            ]
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        TossPaymentCustomerInfo payment = tossPaymentsOrderService.getPaymentByOrderId("order-456");

        assertThat(payment.customerPhone()).isNull();
    }

    @Test
    @DisplayName("orderItems 상품명이 없으면 orderName fallback을 사용한다")
    void getPaymentByOrderIdFallsBackToOrderName() {
        mockRestServiceServer.expect(requestTo("https://linkpay-openapi.tosspayments.com/api/v1/orders/order-789"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "code": "SUCCESS",
                          "message": "",
                          "result": {
                            "paymentKey": "payment-key",
                            "orderId": "order-789",
                            "orderName": "테스트",
                            "amount": 100,
                            "customerName": "홍길동",
                            "customerPhoneNumber": "01012345678"
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        TossPaymentCustomerInfo payment = tossPaymentsOrderService.getPaymentByOrderId("order-789");

        assertThat(payment.orderName()).isEqualTo("테스트");
    }
}
