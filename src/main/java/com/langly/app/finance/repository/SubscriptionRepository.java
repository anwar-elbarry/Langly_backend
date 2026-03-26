package com.langly.app.finance.repository;

import com.langly.app.finance.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    List<Subscription> findBySchoolId(String schoolId);

    boolean existsBySchoolIdAndStatusIn(String schoolId, java.util.List<com.langly.app.finance.entity.enums.PaymentStatus> statuses);

    java.util.List<Subscription> findAllByStatusAndCurrentPeriodEndBefore(
            com.langly.app.finance.entity.enums.PaymentStatus status, java.time.LocalDate date);
}
