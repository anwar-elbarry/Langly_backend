package com.langly.app.finance.service;

import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.FeePayment;
import com.langly.app.finance.entity.FeeTemplate;
import com.langly.app.finance.repository.FeePaymentRepository;
import com.langly.app.finance.repository.FeeTemplateRepository;
import com.langly.app.finance.web.dto.FeePaymentRequest;
import com.langly.app.finance.web.dto.FeePaymentResponse;
import com.langly.app.finance.web.dto.StudentFeeStatusResponse;
import com.langly.app.finance.web.mapper.FeePaymentMapper;
import com.langly.app.school.entity.School;
import com.langly.app.school.repository.SchoolRepository;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeePaymentServiceImpl implements FeePaymentService {

    private final FeePaymentRepository feePaymentRepository;
    private final FeeTemplateRepository feeTemplateRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final FeePaymentMapper feePaymentMapper;

    @Override
    @Transactional
    public FeePaymentResponse recordPayment(String schoolId, FeePaymentRequest request) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));

        FeeTemplate feeTemplate = feeTemplateRepository.findById(request.getFeeTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("FeeTemplate", request.getFeeTemplateId()));

        if (!feeTemplate.getSchool().getId().equals(schoolId)) {
            throw new IllegalArgumentException("Le frais n'appartient pas à cette école");
        }

        FeePayment payment = new FeePayment();
        payment.setSchool(school);
        payment.setStudent(student);
        payment.setFeeTemplate(feeTemplate);
        payment.setAmount(request.getAmount());
        payment.setPaidAt(request.getPaidAt());
        payment.setNote(request.getNote());
        payment.setIsClosed(false);

        FeePayment saved = feePaymentRepository.save(payment);
        return feePaymentMapper.toResponse(saved);
    }

    @Override
    public List<StudentFeeStatusResponse> getStudentFeeStatuses(String schoolId, String studentId) {
        // Find all active templates for the school
        List<FeeTemplate> activeTemplates = feeTemplateRepository.findAllBySchoolIdAndIsActiveTrue(schoolId);
        
        // Find all payments made by this student in this school
        List<FeePayment> studentPayments = feePaymentRepository.findAllBySchoolIdAndStudentId(schoolId, studentId);

        List<StudentFeeStatusResponse> statuses = new ArrayList<>();

        for (FeeTemplate template : activeTemplates) {
            List<FeePayment> templatePayments = studentPayments.stream()
                    .filter(p -> p.getFeeTemplate().getId().equals(template.getId()))
                    .toList();

            BigDecimal totalPaid = templatePayments.stream()
                    .map(FeePayment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            boolean isClosed = templatePayments.stream().anyMatch(FeePayment::getIsClosed);
            int paymentCount = templatePayments.size();

            String status = "UNPAID";
            if (template.getIsRecurring()) {
                if (isClosed) {
                    status = "PAID";
                } else if (paymentCount > 0) {
                    status = "PARTIALLY_PAID"; // Repeatedly paying but not yet closed
                }
            } else {
                if (totalPaid.compareTo(template.getAmount()) >= 0) {
                    status = "PAID";
                    isClosed = true;
                } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                    status = "PARTIALLY_PAID";
                }
            }

            StudentFeeStatusResponse response = new StudentFeeStatusResponse(
                    template.getId(),
                    template.getName(),
                    template.getAmount(),
                    template.getIsRecurring(),
                    totalPaid,
                    paymentCount,
                    isClosed,
                    status
            );
            statuses.add(response);
        }

        return statuses;
    }

    @Override
    public List<FeePaymentResponse> getPaymentHistory(String schoolId, String studentId, String feeTemplateId) {
        return feePaymentRepository.findAllBySchoolIdAndStudentIdAndFeeTemplateId(schoolId, studentId, feeTemplateId)
                .stream()
                .map(feePaymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void closeRecurringFee(String schoolId, String studentId, String feeTemplateId) {
        List<FeePayment> payments = feePaymentRepository.findAllBySchoolIdAndStudentIdAndFeeTemplateId(schoolId, studentId, feeTemplateId);
        if (!payments.isEmpty()) {
            // Set the most recent or all to closed. Setting all to true is robust.
            for (FeePayment p : payments) {
                p.setIsClosed(true);
            }
            feePaymentRepository.saveAll(payments);
        } else {
            // If they want to close it without any payment? Allow it by creating a 0 amount dummy payment
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
            FeeTemplate feeTemplate = feeTemplateRepository.findById(feeTemplateId)
                    .orElseThrow(() -> new ResourceNotFoundException("FeeTemplate", feeTemplateId));

            FeePayment dummy = new FeePayment();
            dummy.setSchool(school);
            dummy.setStudent(student);
            dummy.setFeeTemplate(feeTemplate);
            dummy.setAmount(BigDecimal.ZERO);
            dummy.setPaidAt(java.time.LocalDate.now());
            dummy.setNote("Fermeture manuelle du frais récurrent");
            dummy.setIsClosed(true);
            feePaymentRepository.save(dummy);
        }
    }

    @Override
    @Transactional
    public void deletePayment(String paymentId) {
        if (!feePaymentRepository.existsById(paymentId)) {
            throw new ResourceNotFoundException("FeePayment", paymentId);
        }
        feePaymentRepository.deleteById(paymentId);
    }
}
