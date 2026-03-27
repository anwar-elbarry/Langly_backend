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

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private LocalDate nextBillDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", unique = true)
    private Enrollment enrollment;

    private String stripePaymentIntentId;

    private String stripeCheckoutSessionId;

    private String invoicePdfUrl;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillingHistory> histories = new ArrayList<>();
}
