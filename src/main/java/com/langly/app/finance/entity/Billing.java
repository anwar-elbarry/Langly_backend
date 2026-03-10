package com.langly.app.finance.entity;

import com.langly.app.course.entity.Enrollment;
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

    @Enumerated(EnumType.ORDINAL)
    private PaymentStatus status;
    private LocalDate nextBillDate;

    /** Méthode de paiement utilisée lors de la confirmation manuelle (CASH ou BANK_TRANSFER) */
    @Enumerated(EnumType.ORDINAL)
    private PaymentMethod paymentMethod;

    /** Date de paiement, renseignée lors de la confirmation par l'admin */
    private LocalDateTime paidAt;

    /** Lien vers l'inscription associée (US04) */
    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    /** Identifiant Stripe PaymentIntent (US04) */
    private String stripePaymentIntentId;

    /** Identifiant Stripe Checkout Session (US04) */
    private String stripeCheckoutSessionId;

    /** URL de la facture PDF générée (US04) */
    private String invoicePdfUrl;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillingHistory> histories = new ArrayList<>();
}
