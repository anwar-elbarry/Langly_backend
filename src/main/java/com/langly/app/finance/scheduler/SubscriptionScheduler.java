package com.langly.app.finance.scheduler;

import com.langly.app.course.entity.enums.SchoolStatus;
import com.langly.app.finance.entity.Subscription;
import com.langly.app.finance.entity.enums.PaymentStatus;
import com.langly.app.finance.repository.SubscriptionRepository;
import com.langly.app.notification.entity.enums.NotificationType;
import com.langly.app.notification.service.NotificationService;
import com.langly.app.school.entity.School;
import com.langly.app.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    /**
     * Runs daily at 8:00 AM — marks expired subscriptions as OVERDUE and suspends their schools.
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void markExpiredSubscriptions() {
        LocalDate today = LocalDate.now();
        List<Subscription> expired = subscriptionRepository
                .findAllByStatusAndCurrentPeriodEndBefore(PaymentStatus.PAID, today);

        for (Subscription subscription : expired) {
            subscription.setStatus(PaymentStatus.OVERDUE);
            subscriptionRepository.save(subscription);

            School school = subscription.getSchool();
            school.setStatus(SchoolStatus.SUSPENDED);

            // Notify school admin(s)
            for (User user : school.getUsers()) {
                try {
                    notificationService.sendNotification(
                            user.getId(),
                            "Abonnement expiré",
                            "L'abonnement de l'école " + school.getName()
                                    + " a expiré. L'école est suspendue jusqu'au renouvellement.",
                            NotificationType.SUBSCRIPTION_EXPIRED,
                            subscription.getId(),
                            "SUBSCRIPTION"
                    );
                } catch (Exception e) {
                    log.error("Erreur lors de la notification d'expiration pour l'abonnement {}", subscription.getId(), e);
                }
            }

            log.info("Subscription {} expired — school {} suspended", subscription.getId(), school.getId());
        }

        if (!expired.isEmpty()) {
            log.info("Marked {} subscriptions as OVERDUE and suspended their schools", expired.size());
        }
    }
}
