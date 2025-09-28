package org.todos.smart.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiResponse<T>(@JsonProperty("success") boolean success, @JsonProperty("message") String message,
                             @JsonProperty("data") T data) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // Getters
    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public T data() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("ApiResponse{success=%b, message=%s, data=%s}", this.success, this.message, this.data);
    }
}