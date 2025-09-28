package org.todos.smart.util;

import com.google.common.base.Supplier;
import org.todos.smart.model.ApiResponse;

public class ErrorHandler {
    public static String executeWithErrorHandling(Supplier<String> operation) {
        try {
            return operation.get();
        } catch (IllegalArgumentException e) {
            return JacksonUtil.stringify(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return JacksonUtil.stringify(ApiResponse.error("Unexpected error: " + e.getMessage()));
        }
    }
}
