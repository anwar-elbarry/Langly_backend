package com.langly.app.finance.service;

import com.langly.app.finance.web.dto.FeeTemplateRequest;
import com.langly.app.finance.web.dto.FeeTemplateResponse;

import java.util.List;

public interface FeeTemplateService {

    List<FeeTemplateResponse> getAllBySchoolId(String schoolId);

    FeeTemplateResponse create(String schoolId, FeeTemplateRequest request);

    FeeTemplateResponse update(String feeTemplateId, FeeTemplateRequest request);

    void delete(String feeTemplateId);
}
