package com.langly.app.finance.service;

import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.BillingSetting;
import com.langly.app.finance.repository.BillingSettingRepository;
import com.langly.app.finance.web.dto.BillingSettingRequest;
import com.langly.app.finance.web.dto.BillingSettingResponse;
import com.langly.app.finance.web.mapper.BillingSettingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillingSettingServiceImpl implements BillingSettingService {

    private final BillingSettingRepository billingSettingRepository;
    private final BillingSettingMapper billingSettingMapper;

    @Override
    public BillingSettingResponse getBySchoolId(String schoolId) {
        BillingSetting setting = billingSettingRepository.findBySchoolId(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("BillingSetting", "schoolId=" + schoolId));
        return billingSettingMapper.toResponse(setting);
    }

    @Override
    @Transactional
    public BillingSettingResponse update(String schoolId, BillingSettingRequest request) {
        BillingSetting setting = billingSettingRepository.findBySchoolId(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("BillingSetting", "schoolId=" + schoolId));

        billingSettingMapper.updateEntity(setting, request);
        return billingSettingMapper.toResponse(billingSettingRepository.save(setting));
    }
}
