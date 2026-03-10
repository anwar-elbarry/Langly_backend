package com.langly.app.student.web.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentUpdateRequest {
    @Size(max = 20)
    private String cnie;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
}
