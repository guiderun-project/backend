package com.guide.run.global.sms.cool;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class WebhookCoolSmsService {

    @Value("${spring.coolsms.webhook-sms.from-number}")
    private String fromNumber;

    @Value("${spring.coolsms.webhook-sms.api-key}")
    private String apiKey;

    @Value("${spring.coolsms.webhook-sms.api-secret}")
    private String apiSecret;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendPaymentCompletedMessage(String to, String customerName) {
        String normalizedTo = normalizePhoneNumber(to);
        if (!StringUtils.hasText(normalizedTo)) {
            log.warn("Skip webhook sms because customer phone number is invalid. phoneNumber={}", to);
            return;
        }

        try {
            Message message = new Message();
            message.setFrom(fromNumber);
            message.setTo(normalizedTo);
            message.setAutoTypeDetect(true);
            message.setText(buildMessageText(customerName));

            messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) {
            log.error("Failed to send webhook sms. to={}", normalizedTo, e);
        }
    }

    String normalizePhoneNumber(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            return null;
        }

        String normalized = phoneNumber.replaceAll("[^0-9]", "");
        return StringUtils.hasText(normalized) ? normalized : null;
    }

    String buildMessageText(String customerName) {
        String firstLine = StringUtils.hasText(customerName)
                ? customerName.trim() + "님 회비 납부를 완료해주셔서 감사합니다."
                : "안녕하세요, 가이드런프로젝트입니다.";

        return firstLine + "\n"
                + "아래 링크를 통해 정회원 오픈채팅방에 입장해주세요.\n"
                + "*입장 시 대화명은 본인 성함으로 바꿔서 입장!\n"
                + "\n"
                + "비밀번호[ 0401 ]\n"
                + "https://open.kakao.com/o/gLM7V1ni";
    }
}
