package com.langly.app.course.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApproveEnrollmentRequest {

    private List<String> discountIds;
}
