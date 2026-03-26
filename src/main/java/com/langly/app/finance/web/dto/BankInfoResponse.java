package com.langly.app.finance.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BankInfoResponse {
    private String id;
    private String bankName;
    private String accountHolder;
    private String iban;
    private String motive;
    private String note;
}
