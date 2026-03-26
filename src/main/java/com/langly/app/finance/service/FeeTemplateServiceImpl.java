package com.langly.app.finance.service;

import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.FeeTemplate;
import com.langly.app.finance.repository.FeeTemplateRepository;
import com.langly.app.finance.web.dto.FeeTemplateRequest;
import com.langly.app.finance.web.dto.FeeTemplateResponse;
import com.langly.app.finance.web.mapper.FeeTemplateMapper;
import com.langly.app.school.entity.School;
import com.langly.app.school.repository.SchoolRepository;
import com.langly.app.school.exception.SchoolNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeeTemplateServiceImpl implements FeeTemplateService {

    private final FeeTemplateRepository feeTemplateRepository;
    private final FeeTemplateMapper feeTemplateMapper;
    private final SchoolRepository schoolRepository;

    @Override
    public List<FeeTemplateResponse> getAllBySchoolId(String schoolId) {
        return feeTemplateRepository.findAllBySchoolId(schoolId)
                .stream().map(feeTemplateMapper::toResponse).toList();
    }

    @Override
    public List<FeeTemplateResponse> getAllActiveBySchoolId(String schoolId) {
        return feeTemplateRepository.findAllBySchoolIdAndIsActiveTrue(schoolId)
                .stream().map(feeTemplateMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public FeeTemplateResponse create(String schoolId, FeeTemplateRequest request) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new SchoolNotFoundException("id", schoolId));

        FeeTemplate template = new FeeTemplate();
        template.setSchool(school);
        template.setName(request.getName());
        template.setAmount(request.getAmount());
        template.setIsRecurring(request.getIsRecurring());
        template.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        return feeTemplateMapper.toResponse(feeTemplateRepository.save(template));
    }

    @Override
    @Transactional
    public FeeTemplateResponse update(String feeTemplateId, FeeTemplateRequest request) {
        FeeTemplate template = feeTemplateRepository.findById(feeTemplateId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeTemplate", feeTemplateId));

        template.setName(request.getName());
        template.setAmount(request.getAmount());
        template.setIsRecurring(request.getIsRecurring());
        if (request.getIsActive() != null) {
            template.setIsActive(request.getIsActive());
        }

        return feeTemplateMapper.toResponse(feeTemplateRepository.save(template));
    }

    @Override
    @Transactional
    public void delete(String feeTemplateId) {
        FeeTemplate template = feeTemplateRepository.findById(feeTemplateId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeTemplate", feeTemplateId));
        template.setIsActive(false);
        feeTemplateRepository.save(template);
    }
}
