package com.langly.app.student.service;

import com.langly.app.student.web.dto.StudentResponse;

import java.util.List;

public interface StudentService {
    StudentResponse getById(String id);
    List<StudentResponse> getAllBySchoolId(String schoolId);
    /** US-AD-05 : etudiants dont CNIE, birthDate ou phoneNumber est manquant */
    List<StudentResponse> getIncompleteBySchoolId(String schoolId);
}
