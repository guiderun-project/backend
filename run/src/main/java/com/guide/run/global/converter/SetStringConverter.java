package com.guide.run.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

@Converter
public class SetStringConverter implements AttributeConverter<Set<Long>, String> {
    private static final String SPLIT = ",";

    @Override
    public String convertToDatabaseColumn(Set<Long> longs) {
        if (longs == null || longs.isEmpty()) {
            return null;
        }
        // 각 Long 값을 String으로 변환 후 쉼표로 연결
        return longs.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(SPLIT));
    }

    @Override
    public Set<Long> convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        // 쉼표로 구분된 문자열 각 요소를 Long으로 변환 후 Set으로 변경
        return Arrays.stream(s.split(SPLIT))
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }
}
