package com.guide.run.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 메시지 번들이 특정 로케일 전용(exception_ko)이면 요청 로케일에 맞는 번들이 없을 때
 * JVM 시스템 로케일로 fallback하므로, 서버/CI가 en_US면 메시지 대신 키(unknown.code)가 응답에 노출된다.
 */
class MessageConfigTest {

    @Test void resolvesMessagesRegardlessOfRequestAndSystemLocale() {
        Locale systemLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            ResourceBundle.clearCache();
            MessageSource messageSource = new MessageConfig().messageSource("i18n/exception", "UTF-8");

            assertEquals("0000", messageSource.getMessage("unknown.code", null, Locale.KOREAN));
            assertEquals("0000", messageSource.getMessage("unknown.code", null, Locale.ENGLISH));
        } finally {
            Locale.setDefault(systemLocale);
            ResourceBundle.clearCache();
        }
    }
}
