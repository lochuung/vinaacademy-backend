package com.vinaacademy.platform.feature.common.response;

import lombok.*;

import java.time.Instant;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private Instant timestamp;

    public ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("ERROR", message, null);
    }

}