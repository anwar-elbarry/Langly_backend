package com.langly.app.notification.repository;

import com.langly.app.notification.entity.Notification;
import com.langly.app.notification.entity.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findAllByRecipientIdAndStatusOrderByCreatedAtDesc(String recipientId, NotificationStatus status);
    List<Notification> findAllByRecipientIdOrderByCreatedAtDesc(String recipientId);
    long countByRecipientIdAndStatus(String recipientId, NotificationStatus status);
    void deleteAllByRecipientId(String recipientId);
}
