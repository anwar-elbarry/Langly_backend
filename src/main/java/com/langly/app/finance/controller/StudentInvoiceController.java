package com.langly.app.finance.controller;

import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.service.InvoiceService;
import com.langly.app.finance.web.dto.InvoiceResponse;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students/me/invoices")
@RequiredArgsConstructor
@Tag(name = "Student — Invoices", description = "Factures de l'étudiant connecté")
public class StudentInvoiceController {

    private final InvoiceService invoiceService;
    private final StudentRepository studentRepository;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mes factures")
    public ResponseEntity<List<InvoiceResponse>> getMyInvoices(@AuthenticationPrincipal User user) {
        Student student = getStudent(user);
        return ResponseEntity.ok(invoiceService.getAllByStudentId(student.getId()));
    }

    @GetMapping("/{invoiceId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Détail d'une de mes factures")
    public ResponseEntity<InvoiceResponse> getInvoiceDetail(
            @AuthenticationPrincipal User user,
            @PathVariable String invoiceId) {
        Student student = getStudent(user);
        InvoiceResponse invoice = invoiceService.getById(invoiceId);

        // Ownership check
        if (!invoice.getStudentId().equals(student.getId())) {
            throw new IllegalStateException("Cette facture ne vous appartient pas");
        }

        return ResponseEntity.ok(invoice);
    }

    private Student getStudent(User user) {
        return studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + user.getId()));
    }
}
