package com.organlink.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for all REST endpoints
 * Provides consistent response format across the application
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;
    private MetaData meta;

    // Constructors
    public ApiResponse() {
        this.meta = new MetaData();
    }

    public ApiResponse(boolean success, String message, T data) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(boolean success, String message, T data, ErrorDetails error) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    // Static factory methods for success responses
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // Static factory methods for error responses
    public static <T> ApiResponse<T> error(String message, String errorCode, String details) {
        ErrorDetails errorDetails = new ErrorDetails(errorCode, details);
        return new ApiResponse<>(false, message, null, errorDetails);
    }

    public static <T> ApiResponse<T> error(String message, ErrorDetails error) {
        return new ApiResponse<>(false, message, null, error);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }

    public MetaData getMeta() {
        return meta;
    }

    public void setMeta(MetaData meta) {
        this.meta = meta;
    }

    /**
     * Error details for failed responses
     */
    public static class ErrorDetails {
        private String code;
        private String details;

        public ErrorDetails() {}

        public ErrorDetails(String code, String details) {
            this.code = code;
            this.details = details;
        }

        // Getters and Setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }

    /**
     * Metadata for responses
     */
    public static class MetaData {
        private LocalDateTime timestamp;
        private String version;

        public MetaData() {
            this.timestamp = LocalDateTime.now();
            this.version = "1.0.0";
        }

        // Getters and Setters
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", error=" + error +
                ", meta=" + meta +
                '}';
    }
}
