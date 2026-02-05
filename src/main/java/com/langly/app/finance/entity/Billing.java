package com.langly.app.finance.entity;

import com.langly.app.student.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.langly.app.finance.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany
    private List<BillingHistory> histories = new ArrayList<>();
}
