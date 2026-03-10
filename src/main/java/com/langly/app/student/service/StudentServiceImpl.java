package com.langly.app.student.service;

import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.student.web.dto.AdminStudentUpdateRequest;
import com.langly.app.student.web.dto.StudentResponse;
import com.langly.app.student.web.dto.StudentUpdateRequest;
import com.langly.app.student.web.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponse getById(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        StudentResponse response = studentMapper.toResponse(student);
        response.setMissingFields(computeMissingFields(student));
        return response;
    }

    @Override
    public StudentResponse getByUserId(String userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + userId));
        StudentResponse response = studentMapper.toResponse(student);
        response.setMissingFields(computeMissingFields(student));
        return response;
    }

    @Override
    public List<StudentResponse> getAllBySchoolId(String schoolId) {
        return studentRepository.findAllByUserSchoolId(schoolId)
                .stream()
                .map(s -> {
                    StudentResponse r = studentMapper.toResponse(s);
                    r.setMissingFields(computeMissingFields(s));
                    return r;
                }).toList();
    }

    @Override
    public List<StudentResponse> getIncompleteBySchoolId(String schoolId) {
        return studentRepository.findIncompleteBySchoolId(schoolId)
                .stream()
                .map(s -> {
                    StudentResponse r = studentMapper.toResponse(s);
                    r.setMissingFields(computeMissingFields(s));
                    return r;
                }).toList();
    }

    @Override
    @Transactional
    public StudentResponse updateByStudent(String userId, StudentUpdateRequest request) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + userId));

        if (request.getCnie() != null) student.setCNIE(request.getCnie());
        if (request.getBirthDate() != null) student.setBirthDate(request.getBirthDate());

        Student saved = studentRepository.save(student);
        StudentResponse response = studentMapper.toResponse(saved);
        response.setMissingFields(computeMissingFields(saved));
        return response;
    }

    @Override
    @Transactional
    public StudentResponse updateByAdmin(String studentId, AdminStudentUpdateRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        if (request.getLevel() != null) student.setLevel(request.getLevel());
        if (request.getGender() != null) student.setGender(request.getGender());

        Student saved = studentRepository.save(student);
        StudentResponse response = studentMapper.toResponse(saved);
        response.setMissingFields(computeMissingFields(saved));
        return response;
    }

    /**
     * US-AD-05 : construit la liste des champs obligatoires manquants.
     */
    private List<String> computeMissingFields(Student student) {
        List<String> missing = new ArrayList<>();
        if (student.getCNIE() == null || student.getCNIE().isBlank()) {
            missing.add("cnie");
        }
        if (student.getBirthDate() == null) {
            missing.add("birthDate");
        }
        if (student.getUser() != null &&
                (student.getUser().getPhoneNumber() == null || student.getUser().getPhoneNumber().isBlank())) {
            missing.add("phoneNumber");
        }
        return missing;
    }
}
