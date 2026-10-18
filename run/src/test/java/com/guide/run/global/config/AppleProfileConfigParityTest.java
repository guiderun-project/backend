package com.guide.run.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * prod와 batch 인스턴스는 같은 로드밸런서 뒤에서 동일한 프로덕션 트래픽을 처리하므로
 * Apple 로그인 설정이 한쪽에만 있으면 요청 절반이 503을 받는다.
 */
class AppleProfileConfigParityTest {

    @Test void batchProfileCarriesSameAppleConfigurationAsProd() {
        assertEquals(appleProperties("application-prod.yml"), appleProperties("application-batch.yml"));
    }

    private Map<Object, Object> appleProperties(String resource) {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource(resource));
        Properties properties = yaml.getObject();
        if (properties == null) return Map.of();
        return properties.entrySet().stream()
                .filter(entry -> String.valueOf(entry.getKey()).startsWith("apple."))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
