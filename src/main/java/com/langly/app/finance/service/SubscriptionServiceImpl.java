package com.langly.app.finance.service;

import com.langly.app.course.entity.enums.SchoolStatus;
import com.langly.app.finance.entity.Subscription;
import com.langly.app.finance.entity.SubscriptionHistory;
import com.langly.app.finance.entity.enums.BillingCycle;
import com.langly.app.finance.entity.enums.PaymentStatus;
import com.langly.app.finance.exception.SubscriptionNotFoundException;
import com.langly.app.finance.repository.SubscriptionHistoryRepository;
import com.langly.app.finance.repository.SubscriptionRepository;
import com.langly.app.finance.web.dto.*;
import com.langly.app.notification.entity.enums.NotificationType;
import com.langly.app.notification.service.NotificationService;
import com.langly.app.school.entity.School;
import com.langly.app.school.exception.SchoolNotFoundException;
import com.langly.app.school.repository.SchoolRepository;
import com.langly.app.user.entity.User;
import com.langly.app.user.repository.UserRepository;
import com.langly.app.finance.web.mapper.SubscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final SchoolRepository schoolRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final StripeService stripeService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubscriptionResponse create(SubscriptionRequest request) {
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new SchoolNotFoundException("id", request.getSchoolId()));

        // Prevent duplicate active subscriptions for the same school
        boolean hasActive = subscriptionRepository.existsBySchoolIdAndStatusIn(
                request.getSchoolId(),
                java.util.List.of(PaymentStatus.PAID, PaymentStatus.PENDING, PaymentStatus.PENDING_TRANSFER)
        );
        if (hasActive) {
            throw new IllegalStateException("Cette école possède déjà un abonnement actif ou en attente");
        }

        Subscription subscription = subscriptionMapper.toEntity(request);
        subscription.setSchool(school);
        subscription.setStatus(PaymentStatus.PENDING);
        subscription.setCurrency(normalizeCurrency(request.getCurrency()));

        LocalDate now = LocalDate.now();
        subscription.setCurrentPeriodStart(now);
        subscription.setCurrentPeriodEnd(calculatePeriodEnd(now, request.getBillingCycle()));
        subscription.setNextPaymentDate(calculatePeriodEnd(now, request.getBillingCycle()));

        Subscription saved = subscriptionRepository.save(subscription);
        return subscriptionMapper.toResponse(saved);
    }

    @Override
    public SubscriptionResponse getById(String id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException("id", id));
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public List<SubscriptionResponse> getBySchoolId(String schoolId) {
        return subscriptionRepository.findBySchoolId(schoolId)
                .stream()
                .map(subscriptionMapper::toResponse)
                .toList();
    }

    @Override
    public List<SubscriptionResponse> getAll() {
        return subscriptionRepository.findAll()
                .stream()
                .map(subscriptionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SubscriptionResponse update(String id, SubscriptionUpdateRequest request) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException("id", id));

        boolean amountChanged = request.getAmount() != null && subscription.getAmount() != null
                && request.getAmount().compareTo(subscription.getAmount()) != 0;
        boolean currencyChanged = request.getCurrency() != null
                && !normalizeCurrency(request.getCurrency()).equalsIgnoreCase(
                        normalizeCurrency(subscription.getCurrency()));
        boolean billingChanged = request.getBillingCycle() != null
                && request.getBillingCycle() != subscription.getBillingCycle();

        subscriptionMapper.updateEntity(subscription, request);
        subscription.setCurrency(normalizeCurrency(subscription.getCurrency()));

        // Policy: if current status is PAID, keep the current period and status;
        // new terms apply from the next renewal without suspension.
        if (subscription.getStatus() == PaymentStatus.PAID) {
            // Ensure nextPaymentDate is set for the next renewal
            if (subscription.getCurrentPeriodStart() == null) {
                LocalDate start = LocalDate.now();
                subscription.setCurrentPeriodStart(start);
                LocalDate end = calculatePeriodEnd(start, subscription.getBillingCycle());
                subscription.setCurrentPeriodEnd(end);
                subscription.setNextPaymentDate(end);
            } else if (subscription.getCurrentPeriodEnd() == null) {
                LocalDate end = calculatePeriodEnd(subscription.getCurrentPeriodStart(), subscription.getBillingCycle());
                subscription.setCurrentPeriodEnd(end);
                subscription.setNextPaymentDate(end);
            }
        } else {
            // For PENDING/OVERDUE/CANCELLED etc., apply changes immediately and reset period when billing cycle changes
            if (billingChanged || subscription.getCurrentPeriodStart() == null) {
                LocalDate start = LocalDate.now();
                subscription.setCurrentPeriodStart(start);
                LocalDate end = calculatePeriodEnd(start, subscription.getBillingCycle());
                subscription.setCurrentPeriodEnd(end);
                subscription.setNextPaymentDate(end);
            }
        }

        Subscription updated = subscriptionRepository.save(subscription);
        return subscriptionMapper.toResponse(updated);
    }

    private String normalizeCurrency(String currency) {
        String code = (currency == null || currency.isBlank()) ? "MAD" : currency.trim().toUpperCase();
        // Map legacy/local shorthand DH to ISO 4217 MAD for Stripe compatibility
        if (code.equals("DH")) code = "MAD";
        return code;
    }

    @Override
    @Transactional
    public SubscriptionResponse updatePaymentStatus(String id, PaymentStatusUpdateRequest request) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException("id", id));

        subscription.setStatus(request.getStatus());

        // Record history entry
        SubscriptionHistory history = new SubscriptionHistory();
        history.setAmount(subscription.getAmount());
        history.setStatusAtThatTime(request.getStatus());
        history.setPaidAt(LocalDateTime.now());
        history.setSubscription(subscription);
        subscriptionHistoryRepository.save(history);

        // Update school status based on payment status
        School school = subscription.getSchool();
        if (school != null) {
            switch (request.getStatus()) {
                case PAID -> {
                    school.setStatus(SchoolStatus.ACTIVE);
                    // Advance the billing period
                    LocalDate newPeriodStart = subscription.getCurrentPeriodEnd();
                    subscription.setCurrentPeriodStart(newPeriodStart);
                    subscription.setCurrentPeriodEnd(calculatePeriodEnd(newPeriodStart, subscription.getBillingCycle()));
                    subscription.setNextPaymentDate(calculatePeriodEnd(newPeriodStart, subscription.getBillingCycle()));
                    
                    // Notify school admins
                    List<User> schoolAdmins = userRepository.findAllBySchoolIdAndRoleName(school.getId(), "SCHOOL_ADMIN");
                    String msg = "Votre paiement a été validé. Votre école est maintenant active.";
                    for (User admin : schoolAdmins) {
                        notificationService.sendNotification(
                                admin.getId(),
                                "Abonnement Activé",
                                msg,
                                NotificationType.SUBSCRIPTION_ACTIVATED,
                                subscription.getId(),
                                "SUBSCRIPTION"
                        );
                    }
                }
                case OVERDUE -> school.setStatus(SchoolStatus.SUSPENDED);
                default -> { /* no school status change for PENDING / CANCELLED */ }
            }
            schoolRepository.save(school);
        }

        Subscription updated = subscriptionRepository.save(subscription);
        return subscriptionMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new SubscriptionNotFoundException("id", id);
        }
        subscriptionRepository.deleteById(id);
    }

    private LocalDate calculatePeriodEnd(LocalDate start, BillingCycle billingCycle) {
        return switch (billingCycle) {
            case MONTHLY -> start.plusMonths(1);
            case YEARLY -> start.plusYears(1);
        };
    }

    @Override
    @Transactional
    public PaymentResponse pay(String id, SelectPaymentMethodRequest request) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException("id", id));
        
        com.langly.app.finance.entity.enums.PaymentMethod method = request.getPaymentMethod();
        
        if (method == com.langly.app.finance.entity.enums.PaymentMethod.STRIPE) {
            try {
                com.stripe.model.checkout.Session session = stripeService.createSubscriptionCheckoutSession(subscription);
                return new PaymentResponse(null, session.getUrl());
            } catch (com.stripe.exception.StripeException e) {
                log.error("Erreur lors de la création de la session Stripe", e);
                throw new RuntimeException("Erreur lors de l'initialisation du paiement Stripe", e);
            }
        }
        
        return new PaymentResponse(null, null);
    }
    
    @Override
    @Transactional
    public void declareTransfer(String id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException("id", id));
                
        // Only update if it's currently PENDING
        if (subscription.getStatus() == PaymentStatus.PENDING) {
            subscription.setStatus(PaymentStatus.PENDING_TRANSFER);
            subscriptionRepository.save(subscription);
        }

        String schoolName = subscription.getSchool() != null ? subscription.getSchool().getName() : "Inconnue";
        
        // Find all Super Admins to notify them
        // The role name in the database is "SUPER_ADMIN"
        com.langly.app.Authority.entity.Role superAdminRole = userRepository.findAll().stream()
                .map(User::getRole)
                .filter(r -> r != null && "SUPER_ADMIN".equals(r.getName()))
                .findFirst()
                .orElse(null);

        if (superAdminRole != null) {
            List<User> superAdmins = userRepository.findAllByRole(superAdminRole);
            String msg = "L'école " + schoolName + " a déclaré un virement bancaire pour l'abonnement " + subscription.getId() + ". En attente de validation.";
            for (User admin : superAdmins) {
                notificationService.sendNotification(
                        admin.getId(),
                        "Virement Déclaré",
                        msg,
                        NotificationType.SUBSCRIPTION_TRANSFER_DECLARED,
                        subscription.getId(),
                        "SUBSCRIPTION"
                );
            }
        }
    }
}
