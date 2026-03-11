package com.langly.app.notification.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {
    private String id;
    private String title;
    private String message;
    private String type;
    private String status;
    private LocalDateTime createdAt;
    private String referenceId;
    private String referenceType;
}
