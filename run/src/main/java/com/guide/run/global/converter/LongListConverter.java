package com.guide.run.global.converter;

import jakarta.persistence.AttributeConverter;

import java.util.ArrayList;
import java.util.List;

public class LongListConverter implements AttributeConverter<List<Long>, String> {

    private static final String SPLIT = ",";

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if(attribute == null || attribute.isEmpty()){
            return null;
        }
        return String.join(SPLIT, attribute.stream().map(String::valueOf).toArray(String[]::new));
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        String[] values = dbData.split(SPLIT);
        List<Long> longList = new ArrayList<>();
        for (String value : values) {
            longList.add(Long.valueOf(value));
        }
        return longList;
    }
}
