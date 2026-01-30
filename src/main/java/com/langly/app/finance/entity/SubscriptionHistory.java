package com.langly.app.finance.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.langly.app.finance.entity.enums.PaymentMethod;
import com.langly.app.finance.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data

@Entity
@Table(name = "subscription_history")
public class SubscriptionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private BigDecimal amount;
    private PaymentStatus statusAtThatTime;
    private PaymentMethod paymentMethod;
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @OneToMany
    private List<SubscriptionHistory> histories = new ArrayList<>();
}
