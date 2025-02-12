package com.vinaacademy.platform.exception;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnauthorizedException extends RuntimeException {
    private Integer code;
    private String message;

    public static UnauthorizedException message(String message) {
        return UnauthorizedException.builder()
                .code(401)
                .message(message)
                .build();
    }
}
