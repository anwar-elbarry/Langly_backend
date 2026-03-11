package com.langly.app.finance.service;

import com.langly.app.finance.web.dto.BillingSettingRequest;
import com.langly.app.finance.web.dto.BillingSettingResponse;

public interface BillingSettingService {

    BillingSettingResponse getBySchoolId(String schoolId);

    BillingSettingResponse update(String schoolId, BillingSettingRequest request);
}
