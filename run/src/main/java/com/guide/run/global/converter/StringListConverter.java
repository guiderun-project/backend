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
        if(stringList == null || stringList.isEmpty()){
            return null;
        }
        return String.join(SPLIT, stringList);
    }

    @Override
    public List<String> convertToEntityAttribute(String data) {
        if(data == null){
            return null;
        }
        return Arrays.asList(data.split(SPLIT));
    }
}
