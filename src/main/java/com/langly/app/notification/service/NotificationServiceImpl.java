package com.langly.app.notification.service;

import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.notification.entity.Notification;
import com.langly.app.notification.entity.enums.NotificationStatus;
import com.langly.app.notification.entity.enums.NotificationType;
import com.langly.app.notification.repository.NotificationRepository;
import com.langly.app.notification.web.dto.NotificationResponse;
import com.langly.app.notification.web.mapper.NotificationMapper;
import com.langly.app.user.entity.User;
import com.langly.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void sendNotification(String recipientUserId, String title, String message,
                                 NotificationType type, String referenceId, String referenceType) {
        User recipient = userRepository.findById(recipientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", recipientUserId));

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setRecipient(recipient);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getMyNotifications(String userId) {
        return notificationMapper.toResponseList(
                notificationRepository.findAllByRecipientIdOrderByCreatedAtDesc(userId));
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(String userId) {
        return notificationMapper.toResponseList(
                notificationRepository.findAllByRecipientIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD));
    }

    @Override
    public long getUnreadCount(String userId) {
        return notificationRepository.countByRecipientIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    @Override
    @Transactional
    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        notification.setStatus(NotificationStatus.READ);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        List<Notification> unread = notificationRepository
                .findAllByRecipientIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD);
        unread.forEach(n -> n.setStatus(NotificationStatus.READ));
        notificationRepository.saveAll(unread);
    }

    @Override
    @Transactional
    public void deleteAllMyNotifications(String userId) {
        notificationRepository.deleteAllByRecipientId(userId);
    }
}
