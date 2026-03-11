package com.langly.app.finance.entity;

import com.langly.app.finance.entity.enums.InstallmentPlan;
import com.langly.app.school.entity.School;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "billing_settings")
@NoArgsConstructor
@AllArgsConstructor
public class BillingSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "school_id", nullable = false, unique = true)
    private School school;

    @Column(nullable = false)
    private BigDecimal tvaRate;

    @Column(nullable = false)
    private Integer dueDateDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallmentPlan defaultInstallmentPlan;

    @Column(nullable = false)
    private Boolean blockOnUnpaid;

    @Column(nullable = false)
    private Boolean discountEnabled;
}
