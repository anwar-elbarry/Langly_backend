package com.langly.app.finance.entity;

import com.langly.app.student.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.langly.app.finance.entity.enums.PaymentMethod;
import com.langly.app.finance.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data

@Entity
@Table(name = "billings")

@NoArgsConstructor
@AllArgsConstructor
public class Billing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private BigDecimal price;
    private PaymentStatus status;
    private LocalDate nextBillDate;

    /** Méthode de paiement utilisée lors de la confirmation manuelle (CASH ou BANK_TRANSFER) */
    private PaymentMethod paymentMethod;

    /** Date de paiement, renseignée lors de la confirmation par l'admin */
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillingHistory> histories = new ArrayList<>();
}
