package com.langly.app.finance.service;

import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.Discount;
import com.langly.app.finance.repository.DiscountRepository;
import com.langly.app.finance.web.dto.DiscountRequest;
import com.langly.app.finance.web.dto.DiscountResponse;
import com.langly.app.finance.web.mapper.DiscountMapper;
import com.langly.app.school.entity.School;
import com.langly.app.school.repository.SchoolRepository;
import com.langly.app.school.exception.SchoolNotFoundException;
import com.langly.app.finance.entity.enums.DiscountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final SchoolRepository schoolRepository;

    @Override
    public List<DiscountResponse> getAllBySchoolId(String schoolId) {
        return discountRepository.findAllBySchoolId(schoolId)
                .stream().map(discountMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public DiscountResponse create(String schoolId, DiscountRequest request) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new SchoolNotFoundException("id", schoolId));

        validateDiscountValue(request.getType(), request.getValue());

        Discount discount = new Discount();
        discount.setSchool(school);
        discount.setName(request.getName());
        discount.setType(request.getType());
        discount.setValue(request.getValue());
        discount.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        return discountMapper.toResponse(discountRepository.save(discount));
    }

    @Override
    @Transactional
    public DiscountResponse update(String discountId, DiscountRequest request) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", discountId));

        validateDiscountValue(request.getType(), request.getValue());

        discount.setName(request.getName());
        discount.setType(request.getType());
        discount.setValue(request.getValue());
        if (request.getIsActive() != null) {
            discount.setIsActive(request.getIsActive());
        }

        return discountMapper.toResponse(discountRepository.save(discount));
    }

    @Override
    @Transactional
    public void delete(String discountId) {
        if (!discountRepository.existsById(discountId)) {
            throw new ResourceNotFoundException("Discount", discountId);
        }
        discountRepository.deleteById(discountId);
    }

    private void validateDiscountValue(DiscountType type, java.math.BigDecimal value) {
        if (type == DiscountType.PERCENTAGE && (value.compareTo(java.math.BigDecimal.ZERO) < 0 || value.compareTo(java.math.BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("Le pourcentage de remise doit être entre 0 et 100");
        }
    }
}
