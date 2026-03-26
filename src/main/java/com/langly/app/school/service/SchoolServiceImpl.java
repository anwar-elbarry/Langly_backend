package com.langly.app.school.service;

import com.langly.app.course.entity.enums.SchoolStatus;
import com.langly.app.finance.entity.BillingSetting;
import com.langly.app.finance.entity.enums.InstallmentPlan;
import com.langly.app.finance.repository.BillingSettingRepository;
import com.langly.app.school.entity.School;
import com.langly.app.school.exception.SchoolNotFoundException;
import com.langly.app.school.repository.SchoolRepository;
import com.langly.app.school.web.dto.SchoolRequest;
import com.langly.app.school.web.dto.SchoolResponse;
import com.langly.app.school.web.dto.SchoolUpdateRequest;
import com.langly.app.school.web.mapper.SchoolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;
    private final BillingSettingRepository billingSettingRepository;

    @Override
    @Transactional
    public SchoolResponse create(SchoolRequest request) {
        School school = schoolMapper.toEntity(request);
        school.setStatus(SchoolStatus.PENDING);
        School savedSchool = schoolRepository.save(school);

        // Auto-create default billing settings for the new school
        BillingSetting settings = new BillingSetting();
        settings.setSchool(savedSchool);
        settings.setTvaRate(BigDecimal.valueOf(20));
        settings.setDueDateDays(0);
        settings.setDefaultInstallmentPlan(InstallmentPlan.FULL);
        settings.setBlockOnUnpaid(false);
        settings.setDiscountEnabled(false);
        billingSettingRepository.save(settings);

        return schoolMapper.toResponse(savedSchool);
    }

    @Override
    public SchoolResponse getById(String id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new SchoolNotFoundException("id", id));
        return schoolMapper.toResponse(school);
    }

    @Override
    public List<SchoolResponse> getByName(String name) {
        return schoolRepository.findByName(name)
                .stream()
                .map(schoolMapper::toResponse)
                .toList();
    }

    @Override
    public List<SchoolResponse> getAll() {
        return schoolRepository.findAll()
                .stream()
                .map(schoolMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SchoolResponse update(String id, SchoolUpdateRequest request) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new SchoolNotFoundException("id", id));
        schoolMapper.updateEntity(school, request);
        School updatedSchool = schoolRepository.save(school);
        return schoolMapper.toResponse(updatedSchool);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!schoolRepository.existsById(id)) {
            throw new SchoolNotFoundException("id", id);
        }
        schoolRepository.deleteById(id);
    }
}
