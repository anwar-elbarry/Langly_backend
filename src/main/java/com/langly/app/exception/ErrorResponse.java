package com.langly.app.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
