package com.langly.app.finance.entity;

import com.langly.app.school.entity.School;
import jakarta.persistence.*;
import lombok.Data;
import com.langly.app.finance.entity.enums.BillingCycle;
import com.langly.app.finance.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private BigDecimal amount;
    private String currency;
    private BillingCycle billingCycle;
    private LocalDate currentPeriodStart;
    private LocalDate currentPeriodEnd;
    private PaymentStatus status;
    private LocalDate nextPaymentDate;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
}
