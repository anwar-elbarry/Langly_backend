package com.langly.app.notification.service;

import com.langly.app.notification.entity.enums.NotificationType;
import com.langly.app.notification.web.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void sendNotification(String recipientUserId, String title, String message,
                          NotificationType type, String referenceId, String referenceType);
    List<NotificationResponse> getMyNotifications(String userId);
    List<NotificationResponse> getUnreadNotifications(String userId);
    long getUnreadCount(String userId);
    void markAsRead(String notificationId);
    void markAllAsRead(String userId);
}
