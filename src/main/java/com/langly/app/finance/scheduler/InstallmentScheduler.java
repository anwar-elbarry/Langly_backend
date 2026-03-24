package com.langly.app.finance.scheduler;

import com.langly.app.finance.entity.PaymentSchedule;
import com.langly.app.finance.entity.enums.InstallmentStatus;
import com.langly.app.finance.repository.PaymentScheduleRepository;
import com.langly.app.notification.entity.enums.NotificationType;
import com.langly.app.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstallmentScheduler {

    private final PaymentScheduleRepository paymentScheduleRepository;
    private final NotificationService notificationService;

    /**
     * Runs daily at 8:00 AM — sends reminders for installments due in 3 days.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendInstallmentReminders() {
        LocalDate reminderDate = LocalDate.now().plusDays(3);
        List<PaymentSchedule> upcoming = paymentScheduleRepository
                .findAllByStatusAndDueDate(InstallmentStatus.PENDING, reminderDate);

        for (PaymentSchedule schedule : upcoming) {
            try {
                String userId = schedule.getInvoice().getStudent().getUser().getId();
                String formattedAmount = schedule.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString();
                String invoiceNumber = schedule.getInvoice().getInvoiceNumber();

                notificationService.sendNotification(
                        userId,
                        "Rappel de paiement",
                        "L'échéance n°" + schedule.getInstallment() + " de " + formattedAmount
                                + " MAD pour la facture " + invoiceNumber + " est due le " + schedule.getDueDate() + ".",
                        NotificationType.INSTALLMENT_REMINDER,
                        schedule.getId(),
                        "INSTALLMENT"
                );
                log.info("Installment reminder sent for schedule {} (invoice {})", schedule.getId(), invoiceNumber);
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi du rappel pour l'échéance {}", schedule.getId(), e);
            }
        }

        if (!upcoming.isEmpty()) {
            log.info("Sent {} installment reminders for due date {}", upcoming.size(), reminderDate);
        }
    }

    /**
     * Runs daily at 8:00 AM — marks overdue installments and notifies students.
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void markOverdueInstallments() {
        LocalDate today = LocalDate.now();
        List<PaymentSchedule> overdue = paymentScheduleRepository
                .findAllByStatusAndDueDateBefore(InstallmentStatus.PENDING, today);

        for (PaymentSchedule schedule : overdue) {
            schedule.setStatus(InstallmentStatus.OVERDUE);
            paymentScheduleRepository.save(schedule);

            try {
                String userId = schedule.getInvoice().getStudent().getUser().getId();
                String formattedAmount = schedule.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString();
                String invoiceNumber = schedule.getInvoice().getInvoiceNumber();

                notificationService.sendNotification(
                        userId,
                        "Échéance en retard",
                        "L'échéance n°" + schedule.getInstallment() + " de " + formattedAmount
                                + " MAD pour la facture " + invoiceNumber + " est en retard. Veuillez régulariser votre situation.",
                        NotificationType.INSTALLMENT_OVERDUE,
                        schedule.getId(),
                        "INSTALLMENT"
                );
                log.info("Installment overdue notification sent for schedule {} (invoice {})", schedule.getId(), invoiceNumber);
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de la notification de retard pour l'échéance {}", schedule.getId(), e);
            }
        }

        if (!overdue.isEmpty()) {
            log.info("Marked {} installments as OVERDUE", overdue.size());
        }
    }
}
