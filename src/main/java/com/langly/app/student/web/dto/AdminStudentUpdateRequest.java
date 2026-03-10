package com.langly.app.student.web.dto;

import com.langly.app.course.entity.enums.Level;
import com.langly.app.student.entity.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminStudentUpdateRequest {
    private Level level;
    private Gender gender;
}
