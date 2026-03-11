package com.langly.app.finance.service;

import com.langly.app.finance.web.dto.DiscountRequest;
import com.langly.app.finance.web.dto.DiscountResponse;

import java.util.List;

public interface DiscountService {

    List<DiscountResponse> getAllBySchoolId(String schoolId);

    DiscountResponse create(String schoolId, DiscountRequest request);

    DiscountResponse update(String discountId, DiscountRequest request);

    void delete(String discountId);
}
