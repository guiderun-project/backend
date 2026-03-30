package com.guide.run.global.webhook.tosspayments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.guide.run.global.webhook.tosspayments.dto.TossPaymentCustomerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentsOrderService {

    private static final String ORDER_LOOKUP_URL = "https://api.tosspayments.com/v1/payments/orders/{orderId}";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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

        log.info("TossPayments order lookup response. orderId={}, statusCode={}, body={}",
                orderId,
                response.getStatusCode(),
                maskSensitiveFields(body));

        return new TossPaymentCustomerInfo(
                readText(body, "/paymentKey"),
                readText(body, "/orderId"),
                readText(body, "/orderName"),
                readLong(body, "/totalAmount"),
                firstText(body, List.of("/customerName", "/customer/name")),
                firstText(body, List.of(
                        "/mobilePhone/customerMobilePhone",
                        "/customerMobilePhone",
                        "/mobilePhone",
                        "/customer/mobilePhone",
                        "/customerPhone",
                        "/phone"
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

    private JsonNode maskSensitiveFields(JsonNode body) {
        JsonNode copied = body.deepCopy();
        if (copied instanceof ObjectNode objectNode && objectNode.has("secret")) {
            objectNode.put("secret", "***");
        }
        return copied;
    }
}
