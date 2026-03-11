package com.langly.app.finance.web.mapper;

import com.langly.app.finance.entity.Invoice;
import com.langly.app.finance.entity.InvoiceLine;
import com.langly.app.finance.entity.PaymentSchedule;
import com.langly.app.finance.web.dto.InvoiceLineResponse;
import com.langly.app.finance.web.dto.InvoiceResponse;
import com.langly.app.finance.web.dto.PaymentScheduleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentFullName", expression = "java(invoice.getStudent() != null && invoice.getStudent().getUser() != null ? invoice.getStudent().getUser().getFirstName() + \" \" + invoice.getStudent().getUser().getLastName() : null)")
    @Mapping(target = "schoolId", source = "school.id")
    @Mapping(target = "enrollmentId", source = "enrollment.id")
    @Mapping(target = "courseId", expression = "java(invoice.getEnrollment() != null && invoice.getEnrollment().getCourse() != null ? invoice.getEnrollment().getCourse().getId() : null)")
    @Mapping(target = "courseName", expression = "java(invoice.getEnrollment() != null && invoice.getEnrollment().getCourse() != null ? invoice.getEnrollment().getCourse().getName() : null)")
    @Mapping(target = "status", expression = "java(invoice.getStatus() != null ? invoice.getStatus().name() : null)")
    @Mapping(target = "lines", source = "lines")
    @Mapping(target = "schedules", source = "schedules")
    InvoiceResponse toResponse(Invoice invoice);

    @Mapping(target = "feeTemplateId", source = "feeTemplate.id")
    InvoiceLineResponse toLineResponse(InvoiceLine line);

    List<InvoiceLineResponse> toLineResponseList(List<InvoiceLine> lines);

    @Mapping(target = "status", expression = "java(schedule.getStatus() != null ? schedule.getStatus().name() : null)")
    PaymentScheduleResponse toScheduleResponse(PaymentSchedule schedule);

    List<PaymentScheduleResponse> toScheduleResponseList(List<PaymentSchedule> schedules);
}
