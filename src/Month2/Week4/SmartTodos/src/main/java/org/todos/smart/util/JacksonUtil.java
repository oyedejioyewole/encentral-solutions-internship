package org.todos.smart.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.todos.smart.model.ApiResponse;

public class JacksonUtil {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static ApiResponse<?> parse(String payload) {
        try {
            return objectMapper.readValue(payload, ApiResponse.class);
        } catch (JsonProcessingException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public static <T> T convertValue(Object data, Class<T> dataClass) {
        return objectMapper.convertValue(data, dataClass);
    }

    public static <T> T convertComplexValue(Object data, TypeReference<T> typeReference) {
        return objectMapper.convertValue(data, typeReference);
    }

    public static <T> String stringify(T payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return "{\"success\": false, \"message\": \"Error converting to JSON\"}";
        }
    }
}
