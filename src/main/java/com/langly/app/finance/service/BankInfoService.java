package com.langly.app.finance.service;

import com.langly.app.finance.web.dto.BankInfoResponse;
import com.langly.app.finance.web.dto.BankInfoUpdateRequest;

public interface BankInfoService {
    BankInfoResponse get();
    BankInfoResponse update(BankInfoUpdateRequest request);
}
