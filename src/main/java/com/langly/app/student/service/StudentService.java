package com.langly.app.student.service;

import com.langly.app.student.web.dto.AdminStudentUpdateRequest;
import com.langly.app.student.web.dto.StudentResponse;
import com.langly.app.student.web.dto.StudentUpdateRequest;

import java.util.List;

public interface StudentService {
    StudentResponse getById(String id);
    StudentResponse getByUserId(String userId);
    List<StudentResponse> getAllBySchoolId(String schoolId);
    /** US-AD-05 : etudiants dont CNIE, birthDate ou phoneNumber est manquant */
    List<StudentResponse> getIncompleteBySchoolId(String schoolId);
    StudentResponse updateByStudent(String userId, StudentUpdateRequest request);
    StudentResponse updateByAdmin(String studentId, AdminStudentUpdateRequest request);
}
