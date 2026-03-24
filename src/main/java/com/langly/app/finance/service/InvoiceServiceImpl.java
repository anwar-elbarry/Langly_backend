package com.langly.app.finance.service;

import com.langly.app.course.entity.Enrollment;
import com.langly.app.course.entity.enums.EnrollmentStatus;
import com.langly.app.course.repository.EnrollmentRepository;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.*;
import com.langly.app.finance.entity.enums.*;
import com.langly.app.finance.repository.*;
import com.langly.app.finance.web.dto.FinancialSummaryResponse;
import com.langly.app.finance.web.dto.InvoiceResponse;
import com.langly.app.finance.web.dto.PaymentScheduleResponse;
import com.langly.app.finance.web.dto.RecordPaymentRequest;
import com.langly.app.finance.web.mapper.InvoiceMapper;
import com.langly.app.notification.entity.enums.NotificationType;
import com.langly.app.notification.service.NotificationService;
import com.langly.app.school.entity.School;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final BillingSettingRepository billingSettingRepository;
    private final DiscountRepository discountRepository;
    private final FeeTemplateRepository feeTemplateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final InvoiceMapper invoiceMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public InvoiceResponse generateInvoice(String enrollmentId, List<String> discountIds) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        School school = enrollment.getStudent().getUser().getSchool();
        if (school == null) {
            throw new IllegalStateException("L'étudiant n'est pas rattaché à une école");
        }

        // Fetch billing settings (use defaults if none exist)
        BillingSetting settings = billingSettingRepository.findBySchoolId(school.getId())
                .orElse(null);
        BigDecimal tvaRate = settings != null ? settings.getTvaRate() : BigDecimal.valueOf(20);
        int dueDateDays = settings != null ? settings.getDueDateDays() : 0;

        // Build invoice lines
        List<InvoiceLine> lines = new ArrayList<>();

        // 1. Tuition line from course price
        BigDecimal coursePrice = enrollment.getCourse().getPrice();
        if (coursePrice != null && coursePrice.compareTo(BigDecimal.ZERO) > 0) {
            InvoiceLine tuitionLine = new InvoiceLine();
            tuitionLine.setDescription("Frais de scolarité — " + enrollment.getCourse().getName());
            tuitionLine.setAmount(coursePrice);
            lines.add(tuitionLine);
        }

        // 2. Active fee templates (REGISTRATION, PLACEMENT_TEST) for this school
        List<FeeTemplate> activeTemplates = feeTemplateRepository.findAllBySchoolIdAndIsActiveTrue(school.getId());
        for (FeeTemplate template : activeTemplates) {
            // Skip TUITION type — we already use course.price
            if (template.getType() == FeeType.TUITION) continue;

            InvoiceLine feeLine = new InvoiceLine();
            feeLine.setDescription(template.getName());
            feeLine.setAmount(template.getAmount());
            feeLine.setFeeTemplate(template);
            lines.add(feeLine);
        }

        // 3. Apply discounts as negative line items
        if (discountIds != null && !discountIds.isEmpty()) {
            BigDecimal positiveTotal = lines.stream()
                    .map(InvoiceLine::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            for (String discountId : discountIds) {
                Discount discount = discountRepository.findById(discountId).orElse(null);
                if (discount == null || !Boolean.TRUE.equals(discount.getIsActive())) continue;

                BigDecimal discountAmount;
                if (discount.getType() == DiscountType.PERCENTAGE) {
                    discountAmount = positiveTotal.multiply(discount.getValue())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                } else {
                    discountAmount = discount.getValue();
                }

                InvoiceLine discountLine = new InvoiceLine();
                discountLine.setDescription("Réduction : " + discount.getName());
                discountLine.setAmount(discountAmount.negate());
                lines.add(discountLine);
            }
        }

        // Compute totals — prices are TTC (tax-inclusive)
        BigDecimal totalTtc = lines.stream()
                .map(InvoiceLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalTtc.compareTo(BigDecimal.ZERO) < 0) {
            totalTtc = BigDecimal.ZERO;
        }

        // Extract HT from TTC: subtotal = totalTtc / (1 + tvaRate/100)
        BigDecimal divisor = BigDecimal.ONE.add(tvaRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        BigDecimal subtotal = totalTtc.divide(divisor, 2, RoundingMode.HALF_UP);
        BigDecimal tvaAmount = totalTtc.subtract(subtotal);

        // Compute due date
        LocalDate courseStart = enrollment.getCourse().getStartDate();
        LocalDate dueDate = courseStart != null ? courseStart.plusDays(dueDateDays) : LocalDate.now();

        // Generate invoice number: INV-YYYY-NNNN
        String invoiceNumber = generateInvoiceNumber(school.getId());

        // Build and save invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setStudent(enrollment.getStudent());
        invoice.setSchool(school);
        invoice.setEnrollment(enrollment);
        invoice.setSubtotal(subtotal);
        invoice.setTvaRate(tvaRate);
        invoice.setTvaAmount(tvaAmount);
        invoice.setTotalTtc(totalTtc);
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setDueDate(dueDate);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Save lines
        for (InvoiceLine line : lines) {
            line.setInvoice(savedInvoice);
        }
        invoiceLineRepository.saveAll(lines);
        savedInvoice.setLines(lines);

        // Send notification to student (show TTC amount — what they actually pay)
        try {
            String userId = enrollment.getStudent().getUser().getId();
            String formattedTotal = totalTtc.setScale(2, RoundingMode.HALF_UP).toPlainString();
            notificationService.sendNotification(
                    userId,
                    "Nouvelle facture : " + invoiceNumber,
                    "Facture " + invoiceNumber + " — " + formattedTotal + " MAD — Échéance : " + dueDate,
                    NotificationType.INVOICE_CREATED,
                    savedInvoice.getId(),
                    "INVOICE"
            );
        } catch (Exception e) {
            log.warn("Impossible d'envoyer la notification pour la facture {}", savedInvoice.getId(), e);
        }

        log.info("Invoice {} generated for enrollment {} — Total TTC: {} MAD",
                invoiceNumber, enrollmentId, totalTtc);

        return invoiceMapper.toResponse(savedInvoice);
    }

    @Override
    public InvoiceResponse getById(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));
        return invoiceMapper.toResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> getAllBySchoolId(String schoolId) {
        return invoiceRepository.findAllBySchoolId(schoolId)
                .stream().map(invoiceMapper::toResponse).toList();
    }

    @Override
    public List<InvoiceResponse> getAllByStudentId(String studentId) {
        return invoiceRepository.findAllByStudentId(studentId)
                .stream().map(invoiceMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public InvoiceResponse recordPayment(String invoiceId, RecordPaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Cette facture est déjà payée");
        }

        BigDecimal paymentAmount = request.getAmount();

        // If installment plan exists, pay the next PENDING installment
        List<PaymentSchedule> schedules = paymentScheduleRepository
                .findAllByInvoiceIdOrderByInstallmentAsc(invoiceId);

        if (!schedules.isEmpty()) {
            PaymentSchedule nextPending = schedules.stream()
                    .filter(s -> s.getStatus() == InstallmentStatus.PENDING)
                    .findFirst()
                    .orElse(null);

            if (nextPending != null) {
                nextPending.setStatus(InstallmentStatus.PAID);
                nextPending.setPaidAt(LocalDateTime.now());
                paymentScheduleRepository.save(nextPending);
            }
        }

        // Compute total paid from schedules
        BigDecimal totalPaid;
        if (!schedules.isEmpty()) {
            totalPaid = schedules.stream()
                    .filter(s -> s.getStatus() == InstallmentStatus.PAID)
                    .map(PaymentSchedule::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            // No installment plan — direct payment
            totalPaid = paymentAmount;
        }

        // Update invoice status — student pays totalTtc (the full price set by admin)
        if (totalPaid.compareTo(invoice.getTotalTtc()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);

            // Transition enrollment APPROVED → IN_PROGRESS
            if (invoice.getEnrollment() != null
                    && invoice.getEnrollment().getStatus() == EnrollmentStatus.APPROVED) {
                invoice.getEnrollment().setStatus(EnrollmentStatus.IN_PROGRESS);
                enrollmentRepository.save(invoice.getEnrollment());
            }
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        Invoice saved = invoiceRepository.save(invoice);

        // Notify student
        try {
            String userId = invoice.getStudent().getUser().getId();
            String formattedAmount = paymentAmount.setScale(2, RoundingMode.HALF_UP).toPlainString();
            notificationService.sendNotification(
                    userId,
                    "Paiement reçu",
                    "Paiement de " + formattedAmount + " MAD reçu pour la facture " + invoice.getInvoiceNumber(),
                    NotificationType.PAYMENT_RECEIVED,
                    saved.getId(),
                    "INVOICE"
            );
        } catch (Exception e) {
            log.warn("Impossible d'envoyer la notification de paiement pour facture {}", invoiceId, e);
        }

        return invoiceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public List<PaymentScheduleResponse> createInstallmentPlan(String invoiceId, InstallmentPlan plan) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Impossible de créer un plan pour une facture déjà payée");
        }

        // Delete existing schedules
        List<PaymentSchedule> existing = paymentScheduleRepository.findAllByInvoiceId(invoiceId);
        paymentScheduleRepository.deleteAll(existing);

        // Student pays totalTtc (the full price set by admin, tax-inclusive)
        BigDecimal total = invoice.getTotalTtc();
        LocalDate courseStart = invoice.getEnrollment() != null && invoice.getEnrollment().getCourse() != null
                ? invoice.getEnrollment().getCourse().getStartDate()
                : LocalDate.now();
        if (courseStart == null) courseStart = LocalDate.now();

        List<PaymentSchedule> schedules = new ArrayList<>();

        switch (plan) {
            case FULL -> {
                schedules.add(createScheduleEntry(invoice, 1, total, courseStart));
            }
            case TWO_PARTS -> {
                BigDecimal half = total.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                BigDecimal remainder = total.subtract(half);
                schedules.add(createScheduleEntry(invoice, 1, half, courseStart));
                schedules.add(createScheduleEntry(invoice, 2, remainder, courseStart.plusWeeks(4)));
            }
            case THREE_PARTS -> {
                BigDecimal first = total.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                BigDecimal quarter = total.subtract(first).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                BigDecimal last = total.subtract(first).subtract(quarter);
                schedules.add(createScheduleEntry(invoice, 1, first, courseStart));
                schedules.add(createScheduleEntry(invoice, 2, quarter, courseStart.plusWeeks(4)));
                schedules.add(createScheduleEntry(invoice, 3, last, courseStart.plusWeeks(8)));
            }
        }

        List<PaymentSchedule> saved = paymentScheduleRepository.saveAll(schedules);
        return saved.stream().map(invoiceMapper::toScheduleResponse).toList();
    }

    @Override
    public List<PaymentScheduleResponse> getSchedule(String invoiceId) {
        return paymentScheduleRepository.findAllByInvoiceIdOrderByInstallmentAsc(invoiceId)
                .stream().map(invoiceMapper::toScheduleResponse).toList();
    }

    @Override
    public FinancialSummaryResponse getFinancialSummary(String schoolId) {
        List<Invoice> invoices = invoiceRepository.findAllBySchoolId(schoolId);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalTva = BigDecimal.ZERO;
        BigDecimal paidRevenue = BigDecimal.ZERO;
        BigDecimal paidTva = BigDecimal.ZERO;
        BigDecimal pendingRevenue = BigDecimal.ZERO;
        BigDecimal pendingTva = BigDecimal.ZERO;
        long paidCount = 0;
        long unpaidCount = 0;
        BigDecimal tvaRate = BigDecimal.valueOf(20);

        BillingSetting settings = billingSettingRepository.findBySchoolId(schoolId).orElse(null);
        if (settings != null) {
            tvaRate = settings.getTvaRate();
        }

        for (Invoice inv : invoices) {
            totalRevenue = totalRevenue.add(inv.getSubtotal());
            totalTva = totalTva.add(inv.getTvaAmount());

            if (inv.getStatus() == InvoiceStatus.PAID) {
                paidRevenue = paidRevenue.add(inv.getSubtotal());
                paidTva = paidTva.add(inv.getTvaAmount());
                paidCount++;
            } else {
                pendingRevenue = pendingRevenue.add(inv.getSubtotal());
                pendingTva = pendingTva.add(inv.getTvaAmount());
                unpaidCount++;
            }
        }

        BigDecimal totalTtc = totalRevenue.add(totalTva);

        return new FinancialSummaryResponse(
                totalRevenue, totalTva, totalTtc,
                paidRevenue, paidTva,
                pendingRevenue, pendingTva,
                invoices.size(), paidCount, unpaidCount,
                tvaRate
        );
    }

    // --- Private helpers ---

    private String generateInvoiceNumber(String schoolId) {
        int year = Year.now().getValue();
        long count = invoiceRepository.countBySchoolId(schoolId);
        return String.format("INV-%d-%04d", year, count + 1);
    }

    private PaymentSchedule createScheduleEntry(Invoice invoice, int installment, BigDecimal amount, LocalDate dueDate) {
        PaymentSchedule schedule = new PaymentSchedule();
        schedule.setInvoice(invoice);
        schedule.setInstallment(installment);
        schedule.setAmount(amount);
        schedule.setDueDate(dueDate);
        schedule.setStatus(InstallmentStatus.PENDING);
        return schedule;
    }
}
