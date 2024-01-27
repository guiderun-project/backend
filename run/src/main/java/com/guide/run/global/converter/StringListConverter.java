package com.guide.run.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT = ",";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        //todo : 에러코드 추가해야함
        return String.join(SPLIT, stringList);
    }

    @Override
    public List<String> convertToEntityAttribute(String data) {
        return Arrays.asList(data.split(SPLIT));
    }
}
