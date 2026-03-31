package com.guide.run.global.webhook.tosspayments.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.guide.run.global.webhook.tosspayments.dto.TossPaymentCustomerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TossPaymentsOrderService {

    private static final String ORDER_LOOKUP_URL = "https://linkpay-openapi.tosspayments.com/api/v1/orders/{orderId}";

    private final RestTemplate restTemplate;

    @Value("${spring.tosspayments.secret-key}")
    private String secretKey;

    public TossPaymentCustomerInfo getPaymentByOrderId(String orderId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, createAuthorizationHeader());

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                ORDER_LOOKUP_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class,
                orderId
        );

        JsonNode body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("TossPayments payment response body is empty.");
        }

        JsonNode resultNode = body.path("result");
        if (resultNode.isMissingNode() || resultNode.isNull() || resultNode.isEmpty()) {
            throw new IllegalStateException("LinkPay order lookup result is empty.");
        }

        return new TossPaymentCustomerInfo(
                readText(resultNode, "/paymentKey"),
                readText(resultNode, "/orderId"),
                firstText(resultNode, List.of(
                        "/orderItems/0/product/name",
                        "/product/name",
                        "/orderName"
                )),
                firstLong(resultNode, List.of("/amount", "/totalAmount")),
                firstText(resultNode, List.of("/customerName", "/customer/name")),
                firstText(body, List.of(
                        "/result/customerPhoneNumber",
                        "/result/phoneNumber",
                        "/result/customerPhone",
                        "/result/mobilePhone",
                        "/result/shipping/phoneNumber"
                ))
        );
    }

    private String createAuthorizationHeader() {
        String credentials = secretKey + ":";
        String encoded = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    private String firstText(JsonNode body, List<String> jsonPointers) {
        for (String jsonPointer : jsonPointers) {
            String value = readText(body, jsonPointer);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String readText(JsonNode body, String jsonPointer) {
        JsonNode node = body.at(jsonPointer);
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }

        String value = node.asText();
        return StringUtils.hasText(value) ? value : null;
    }

    private Long readLong(JsonNode body, String jsonPointer) {
        JsonNode node = body.at(jsonPointer);
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        return node.asLong();
    }

    private Long firstLong(JsonNode body, List<String> jsonPointers) {
        for (String jsonPointer : jsonPointers) {
            Long value = readLong(body, jsonPointer);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
