package com.langly.app.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "bank_info")
@NoArgsConstructor
public class BankInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String bankName = "";

    @Column(nullable = false)
    private String accountHolder = "";

    @Column(nullable = false)
    private String iban = "";

    @Column(nullable = false)
    private String motive = "";

    @Column
    private String note;
}
