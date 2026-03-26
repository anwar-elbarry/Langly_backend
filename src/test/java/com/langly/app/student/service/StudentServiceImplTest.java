package com.langly.app.student.service;

import com.langly.app.course.entity.enums.Level;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.student.entity.Student;
import com.langly.app.student.entity.enums.Gender;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.student.web.dto.AdminStudentUpdateRequest;
import com.langly.app.student.web.dto.StudentResponse;
import com.langly.app.student.web.dto.StudentUpdateRequest;
import com.langly.app.student.web.mapper.StudentMapper;
import com.langly.app.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock private StudentRepository studentRepository;
    @Mock private StudentMapper studentMapper;

    @InjectMocks private StudentServiceImpl studentService;

    private Student student;
    private User user;
    private StudentResponse studentResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-1");
        user.setFirstName("Alice");
        user.setLastName("Martin");
        user.setEmail("alice@example.com");
        user.setPhoneNumber("0612345678");

        student = new Student();
        student.setId("student-1");
        student.setUser(user);
        student.setCNIE("AB123456");
        student.setBirthDate(LocalDate.of(2000, 5, 15));
        student.setLevel(Level.A2);
        student.setGender(Gender.FEMALE);

        studentResponse = new StudentResponse();
        studentResponse.setId("student-1");
        studentResponse.setUserId("user-1");
        studentResponse.setFirstName("Alice");
        studentResponse.setLastName("Martin");
        studentResponse.setCnie("AB123456");
        studentResponse.setLevel("A2");
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("should return student when found")
        void shouldReturnStudent() {
            when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
            when(studentMapper.toResponse(student)).thenReturn(studentResponse);

            StudentResponse result = studentService.getById("student-1");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("student-1");
            assertThat(result.getFirstName()).isEqualTo("Alice");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(studentRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.getById("unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getByUserId()")
    class GetByUserId {

        @Test
        @DisplayName("should return student by user ID")
        void shouldReturnStudentByUserId() {
            when(studentRepository.findByUserId("user-1")).thenReturn(Optional.of(student));
            when(studentMapper.toResponse(student)).thenReturn(studentResponse);

            StudentResponse result = studentService.getByUserId("user-1");

            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo("user-1");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user ID not found")
        void shouldThrowWhenUserIdNotFound() {
            when(studentRepository.findByUserId("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.getByUserId("unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllBySchoolId()")
    class GetAllBySchoolId {

        @Test
        @DisplayName("should return all students for a school")
        void shouldReturnStudents() {
            when(studentRepository.findAllByUserSchoolId("school-1")).thenReturn(List.of(student));
            when(studentMapper.toResponse(student)).thenReturn(studentResponse);

            List<StudentResponse> result = studentService.getAllBySchoolId("school-1");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should return empty list when no students")
        void shouldReturnEmptyList() {
            when(studentRepository.findAllByUserSchoolId("school-1")).thenReturn(List.of());

            List<StudentResponse> result = studentService.getAllBySchoolId("school-1");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getIncompleteBySchoolId()")
    class GetIncompleteBySchoolId {

        @Test
        @DisplayName("should return incomplete students")
        void shouldReturnIncompleteStudents() {
            Student incomplete = new Student();
            incomplete.setId("student-2");
            incomplete.setUser(user);
            incomplete.setCNIE(null);
            incomplete.setBirthDate(null);

            StudentResponse incompleteResponse = new StudentResponse();
            incompleteResponse.setId("student-2");

            when(studentRepository.findIncompleteBySchoolId("school-1")).thenReturn(List.of(incomplete));
            when(studentMapper.toResponse(incomplete)).thenReturn(incompleteResponse);

            List<StudentResponse> result = studentService.getIncompleteBySchoolId("school-1");

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("updateByStudent()")
    class UpdateByStudent {

        @Test
        @DisplayName("should update CNIE and birthDate")
        void shouldUpdateStudentFields() {
            StudentUpdateRequest request = new StudentUpdateRequest();
            request.setCnie("NEW-CNIE");
            request.setBirthDate(LocalDate.of(1999, 1, 1));

            when(studentRepository.findByUserId("user-1")).thenReturn(Optional.of(student));
            when(studentRepository.save(any(Student.class))).thenReturn(student);
            when(studentMapper.toResponse(student)).thenReturn(studentResponse);

            StudentResponse result = studentService.updateByStudent("user-1", request);

            assertThat(result).isNotNull();
            verify(studentRepository).save(student);
            assertThat(student.getCNIE()).isEqualTo("NEW-CNIE");
            assertThat(student.getBirthDate()).isEqualTo(LocalDate.of(1999, 1, 1));
        }

        @Test
        @DisplayName("should not update null fields")
        void shouldNotUpdateNullFields() {
            StudentUpdateRequest request = new StudentUpdateRequest();
            // both null — nothing should change

            when(studentRepository.findByUserId("user-1")).thenReturn(Optional.of(student));
            when(studentRepository.save(any(Student.class))).thenReturn(student);
            when(studentMapper.toResponse(student)).thenReturn(studentResponse);

            studentService.updateByStudent("user-1", request);

            assertThat(student.getCNIE()).isEqualTo("AB123456"); // unchanged
        }

        @Test
        @DisplayName("should throw when student not found by user ID")
        void shouldThrowWhenNotFound() {
            when(studentRepository.findByUserId("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.updateByStudent("unknown", new StudentUpdateRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateByAdmin()")
    class UpdateByAdmin {

        @Test
        @DisplayName("should update level and gender")
        void shouldUpdateAdminFields() {
            AdminStudentUpdateRequest request = new AdminStudentUpdateRequest();
            request.setLevel(Level.B1);
            request.setGender(Gender.MALE);

            when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
            when(studentRepository.save(any(Student.class))).thenReturn(student);
            when(studentMapper.toResponse(student)).thenReturn(studentResponse);

            StudentResponse result = studentService.updateByAdmin("student-1", request);

            assertThat(result).isNotNull();
            assertThat(student.getLevel()).isEqualTo(Level.B1);
            assertThat(student.getGender()).isEqualTo(Gender.MALE);
        }

        @Test
        @DisplayName("should throw when student not found")
        void shouldThrowWhenNotFound() {
            when(studentRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.updateByAdmin("unknown", new AdminStudentUpdateRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
