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
import com.langly.app.finance.web.mapper.SubscriptionMapper;
import com.langly.app.school.entity.School;
import com.langly.app.school.exception.SchoolNotFoundException;
import com.langly.app.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final SchoolRepository schoolRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    @Transactional
    public SubscriptionResponse create(SubscriptionRequest request) {
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new SchoolNotFoundException("id", request.getSchoolId()));

        Subscription subscription = subscriptionMapper.toEntity(request);
        subscription.setSchool(school);
        subscription.setStatus(PaymentStatus.PENDING);

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

        subscriptionMapper.updateEntity(subscription, request);

        // Recalculate period dates if billing cycle changed
        if (request.getBillingCycle() != null) {
            LocalDate periodStart = subscription.getCurrentPeriodStart() != null
                    ? subscription.getCurrentPeriodStart()
                    : LocalDate.now();
            subscription.setCurrentPeriodEnd(calculatePeriodEnd(periodStart, request.getBillingCycle()));
            subscription.setNextPaymentDate(calculatePeriodEnd(periodStart, request.getBillingCycle()));
        }

        Subscription updated = subscriptionRepository.save(subscription);
        return subscriptionMapper.toResponse(updated);
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
}
