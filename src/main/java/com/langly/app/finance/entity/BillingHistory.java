package com.langly.app.finance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.langly.app.finance.entity.enums.PaymentMethod;
import com.langly.app.finance.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "billing_history")
public class BillingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private BigDecimal price;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "billing_id")
    private Billing billing;
}
