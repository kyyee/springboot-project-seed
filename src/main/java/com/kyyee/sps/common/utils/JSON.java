package com.kyyee.sps.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.kyyee.framework.common.exception.ExceptionUtils;
import com.kyyee.sps.common.component.jackson.deser.LocalDateTimeDeserializer;
import com.kyyee.sps.common.component.jackson.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class JSON {
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        mapper.registerModule(simpleModule);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        javaTimeModule.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        javaTimeModule.addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);

        javaTimeModule.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        javaTimeModule.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
        javaTimeModule.addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
        mapper.registerModule(javaTimeModule);

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper mapper() {
        return mapper;
    }

    public static <T> T toBean(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static <T> T toBean(InputStream is, Class<T> clazz) {
        try {
            return mapper.readValue(is, clazz);
        } catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static <T> T toBean(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static <T> List<T> toList(String json, Class<?>... classes) {
        JavaType type = mapper.getTypeFactory().constructParametricType(ArrayList.class, classes);
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        } else {
            try {
                return obj instanceof String ? obj.toString() : mapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                log.error("serialize json error. message:{}", e.getMessage(), e);
                throw ExceptionUtils.unchecked(e);
            }
        }
    }
}
