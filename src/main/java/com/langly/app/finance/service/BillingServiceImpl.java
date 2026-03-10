package com.langly.app.finance.service;

import com.langly.app.course.entity.enums.EnrollmentStatus;
import com.langly.app.course.repository.EnrollmentRepository;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.Billing;
import com.langly.app.finance.entity.BillingHistory;
import com.langly.app.finance.entity.enums.PaymentMethod;
import com.langly.app.finance.entity.enums.PaymentStatus;
import com.langly.app.finance.repository.BillingRepository;
import com.langly.app.finance.web.dto.BillingConfirmRequest;
import com.langly.app.finance.web.dto.BillingResponse;
import com.langly.app.finance.web.mapper.BillingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillingServiceImpl implements BillingService {

    private final BillingRepository billingRepository;
    private final BillingMapper billingMapper;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<BillingResponse> getPendingBySchoolId(String schoolId) {
        return billingRepository.findAllByStudentUserSchoolIdAndStatus(schoolId, PaymentStatus.PENDING)
                .stream().map(billingMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public BillingResponse confirmPayment(String billingId, BillingConfirmRequest request) {
        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", billingId));

        // Seuls PENDING peuvent être confirmés
        if (billing.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Seuls les paiements en attente (PENDING) peuvent être confirmés. Statut actuel : " + billing.getStatus());
        }

        // Seuls CASH et BANK_TRANSFER sont des paiements manuels
        PaymentMethod method = request.getPaymentMethod();
        if (method != PaymentMethod.CASH && method != PaymentMethod.BANK_TRANSFER) {
            throw new IllegalArgumentException("La validation manuelle n'est disponible que pour CASH ou BANK_TRANSFER");
        }

        LocalDateTime now = LocalDateTime.now();

        // Passer le billing à PAID
        billing.setStatus(PaymentStatus.PAID);
        billing.setPaymentMethod(method);
        billing.setPaidAt(now);

        // Créer l'entrée dans l'historique
        BillingHistory history = new BillingHistory();
        history.setPrice(billing.getPrice());
        history.setStatus(PaymentStatus.PAID);
        history.setPaymentMethod(method);
        history.setPaidAt(now);
        history.setBilling(billing);
        billing.getHistories().add(history);

        // Transition enrollment to IN_PROGRESS on payment confirmation
        if (billing.getEnrollment() != null
                && billing.getEnrollment().getStatus() == EnrollmentStatus.APPROVED) {
            billing.getEnrollment().setStatus(EnrollmentStatus.IN_PROGRESS);
            enrollmentRepository.save(billing.getEnrollment());
        }

        return billingMapper.toResponse(billingRepository.save(billing));
    }

    @Override
    public List<BillingResponse> getAllByStudentId(String studentId) {
        return billingRepository.findAllByStudentId(studentId)
                .stream().map(billingMapper::toResponse).toList();
    }

    @Override
    public List<BillingResponse> getAllBySchoolId(String schoolId) {
        return billingRepository.findAllByStudentUserSchoolId(schoolId)
                .stream().map(billingMapper::toResponse).toList();
    }
}
