package com.langly.app.course.service;

import com.langly.app.Authority.entity.Role;
import com.langly.app.course.entity.Course;
import com.langly.app.course.entity.enums.Level;
import com.langly.app.course.repository.CourseRepository;
import com.langly.app.course.repository.SessionRepository;
import com.langly.app.course.web.dto.CourseRequest;
import com.langly.app.course.web.dto.CourseResponse;
import com.langly.app.course.web.mapper.CourseMapper;
import com.langly.app.course.web.mapper.SessionMapper;
import com.langly.app.exception.AlreadyExistsException;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.user.entity.User;
import com.langly.app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock private CourseRepository courseRepository;
    @Mock private UserRepository userRepository;
    @Mock private CourseMapper courseMapper;
    @Mock private SessionRepository sessionRepository;
    @Mock private SessionMapper sessionMapper;

    @InjectMocks private CourseServiceImpl courseService;

    private User teacher;
    private Role teacherRole;
    private Course course;
    private CourseRequest courseRequest;
    private CourseResponse courseResponse;

    @BeforeEach
    void setUp() {
        teacherRole = new Role();
        teacherRole.setName("TEACHER");

        teacher = new User();
        teacher.setId("teacher-1");
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setRole(teacherRole);

        course = new Course();
        course.setId("course-1");
        course.setName("English B1");
        course.setCode("ENG-B1");
        course.setLanguage("English");
        course.setRequiredLevel(Level.A2);
        course.setTargetLevel(Level.B1);
        course.setStartDate(LocalDate.of(2026, 4, 1));
        course.setEndDate(LocalDate.of(2026, 7, 1));
        course.setPrice(BigDecimal.valueOf(1500));
        course.setCapacity(20);
        course.setSessionPerWeek(3);
        course.setMinutesPerSession(90);
        course.setTeacher(teacher);

        courseRequest = new CourseRequest();
        courseRequest.setName("English B1");
        courseRequest.setCode("ENG-B1");
        courseRequest.setLanguage("English");
        courseRequest.setRequiredLevel(Level.A2);
        courseRequest.setTargetLevel(Level.B1);
        courseRequest.setStartDate(LocalDate.of(2026, 4, 1));
        courseRequest.setEndDate(LocalDate.of(2026, 7, 1));
        courseRequest.setPrice(BigDecimal.valueOf(1500));
        courseRequest.setCapacity(20);
        courseRequest.setSessionPerWeek(3);
        courseRequest.setMinutesPerSession(90);
        courseRequest.setTeacherId("teacher-1");

        courseResponse = new CourseResponse();
        courseResponse.setId("course-1");
        courseResponse.setName("English B1");
        courseResponse.setCode("ENG-B1");
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("should create a course successfully")
        void shouldCreateCourse() {
            when(courseRepository.existsByCode("ENG-B1")).thenReturn(false);
            when(userRepository.findById("teacher-1")).thenReturn(Optional.of(teacher));
            when(courseMapper.toEntity(courseRequest)).thenReturn(course);
            when(courseRepository.save(any(Course.class))).thenReturn(course);
            when(courseMapper.toResponse(course)).thenReturn(courseResponse);

            CourseResponse result = courseService.create(courseRequest);

            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo("ENG-B1");
            verify(courseRepository).save(any(Course.class));
        }

        @Test
        @DisplayName("should throw AlreadyExistsException when code is duplicate")
        void shouldThrowWhenCodeExists() {
            when(courseRepository.existsByCode("ENG-B1")).thenReturn(true);

            assertThatThrownBy(() -> courseService.create(courseRequest))
                    .isInstanceOf(AlreadyExistsException.class);

            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when teacher not found")
        void shouldThrowWhenTeacherNotFound() {
            when(courseRepository.existsByCode("ENG-B1")).thenReturn(false);
            when(userRepository.findById("teacher-1")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.create(courseRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when user is not a teacher")
        void shouldThrowWhenUserIsNotTeacher() {
            Role adminRole = new Role();
            adminRole.setName("SCHOOL_ADMIN");
            teacher.setRole(adminRole);

            when(courseRepository.existsByCode("ENG-B1")).thenReturn(false);
            when(userRepository.findById("teacher-1")).thenReturn(Optional.of(teacher));

            assertThatThrownBy(() -> courseService.create(courseRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("professeur");
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("should return course when found")
        void shouldReturnCourse() {
            when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
            when(courseMapper.toResponse(course)).thenReturn(courseResponse);

            CourseResponse result = courseService.getById("course-1");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("course-1");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(courseRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.getById("unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllBySchoolId()")
    class GetAllBySchoolId {

        @Test
        @DisplayName("should return all courses for a school")
        void shouldReturnCourses() {
            when(courseRepository.findAllByTeacherSchoolId("school-1")).thenReturn(List.of(course));
            when(courseMapper.toResponse(course)).thenReturn(courseResponse);

            List<CourseResponse> result = courseService.getAllBySchoolId("school-1");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should return empty list when no courses")
        void shouldReturnEmptyList() {
            when(courseRepository.findAllByTeacherSchoolId("school-1")).thenReturn(List.of());

            List<CourseResponse> result = courseService.getAllBySchoolId("school-1");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("should update course successfully")
        void shouldUpdateCourse() {
            courseRequest.setCode("ENG-B1-NEW");

            when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
            when(courseRepository.existsByCode("ENG-B1-NEW")).thenReturn(false);
            when(userRepository.findById("teacher-1")).thenReturn(Optional.of(teacher));
            when(courseRepository.save(any(Course.class))).thenReturn(course);
            when(courseMapper.toResponse(course)).thenReturn(courseResponse);

            CourseResponse result = courseService.update("course-1", courseRequest);

            assertThat(result).isNotNull();
            verify(courseRepository).save(any(Course.class));
        }

        @Test
        @DisplayName("should throw when updating with duplicate code")
        void shouldThrowWhenDuplicateCode() {
            courseRequest.setCode("ENG-B2");
            when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
            when(courseRepository.existsByCode("ENG-B2")).thenReturn(true);

            assertThatThrownBy(() -> courseService.update("course-1", courseRequest))
                    .isInstanceOf(AlreadyExistsException.class);
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("should delete course successfully")
        void shouldDeleteCourse() {
            when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));

            courseService.delete("course-1");

            verify(courseRepository).delete(course);
        }

        @Test
        @DisplayName("should throw when course not found")
        void shouldThrowWhenNotFound() {
            when(courseRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.delete("unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllByTeacherId()")
    class GetAllByTeacherId {

        @Test
        @DisplayName("should return courses for a teacher")
        void shouldReturnCourses() {
            when(courseRepository.findAllByTeacherId("teacher-1")).thenReturn(List.of(course));
            when(courseMapper.toResponse(course)).thenReturn(courseResponse);

            List<CourseResponse> result = courseService.getAllByTeacherId("teacher-1");

            assertThat(result).hasSize(1);
        }
    }
}
