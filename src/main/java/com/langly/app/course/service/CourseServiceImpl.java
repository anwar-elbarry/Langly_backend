package com.langly.app.course.service;

import com.langly.app.course.entity.Course;
import com.langly.app.course.repository.CourseRepository;
import com.langly.app.course.repository.SessionRepository;
import com.langly.app.course.web.dto.ActiveCourseResponse;
import com.langly.app.course.web.dto.CourseRequest;
import com.langly.app.course.web.dto.CourseResponse;
import com.langly.app.course.web.dto.SessionResponse;
import com.langly.app.course.web.mapper.CourseMapper;
import com.langly.app.course.web.mapper.SessionMapper;
import com.langly.app.exception.AlreadyExistsException;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.user.entity.User;
import com.langly.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    @Override
    @Transactional
    public CourseResponse create(CourseRequest request) {
        if (courseRepository.existsByCode(request.getCode())) {
            throw new AlreadyExistsException("Un cours avec ce code existe déjà : " + request.getCode());
        }

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.getTeacherId()));

        // Vérifier que l'utilisateur est bien un professeur
        if (teacher.getRole() == null || !"TEACHER".equalsIgnoreCase(teacher.getRole().getName())) {
            throw new IllegalArgumentException("L'utilisateur spécifié n'est pas un professeur");
        }

        Course course = courseMapper.toEntity(request);
        course.setTeacher(teacher);

        return courseMapper.toResponse(courseRepository.save(course));
    }

    @Override
    public CourseResponse getById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        return courseMapper.toResponse(course);
    }

    @Override
    public List<CourseResponse> getAllBySchoolId(String schoolId) {
        return courseRepository.findAllByTeacherSchoolId(schoolId)
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CourseResponse update(String id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));

        // Vérifier le code unique si modifié
        if (!course.getCode().equals(request.getCode()) && courseRepository.existsByCode(request.getCode())) {
            throw new AlreadyExistsException("Un cours avec ce code existe déjà : " + request.getCode());
        }

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.getTeacherId()));

        if (teacher.getRole() == null || !"TEACHER".equalsIgnoreCase(teacher.getRole().getName())) {
            throw new IllegalArgumentException("L'utilisateur spécifié n'est pas un professeur");
        }

        course.setName(request.getName());
        course.setCode(request.getCode());
        course.setLanguage(request.getLanguage());
        course.setRequiredLevel(request.getRequiredLevel());
        course.setTargetLevel(request.getTargetLevel());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        course.setPrice(request.getPrice());
        course.setCapacity(request.getCapacity());
        course.setSessionPerWeek(request.getSessionPerWeek());
        course.setMinutesPerSession(request.getMinutesPerSession());
        course.setTeacher(teacher);

        return courseMapper.toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        courseRepository.delete(course);
    }

    @Override
    public List<ActiveCourseResponse> getActiveCoursesForStudent(String studentId) {
        List<Course> activeCourses = courseRepository.findActiveByStudentId(studentId);

        return activeCourses.stream().map(course -> {
            ActiveCourseResponse response = new ActiveCourseResponse();
            response.setId(course.getId());
            response.setName(course.getName());
            response.setCode(course.getCode());
            response.setLanguage(course.getLanguage());
            response.setRequiredLevel(course.getRequiredLevel() != null ? course.getRequiredLevel().name() : null);
            response.setTargetLevel(course.getTargetLevel() != null ? course.getTargetLevel().name() : null);
            response.setPrice(course.getPrice());
            response.setIsActive(course.getIsActive());

            if (course.getTeacher() != null) {
                response.setTeacherFullName(course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName());
            }

            // Prochaines sessions à venir
            List<SessionResponse> upcoming = sessionRepository
                    .findAllByCourseIdAndScheduledAtAfterOrderByScheduledAtAsc(course.getId(), LocalDateTime.now())
                    .stream().map(sessionMapper::toResponse).toList();
            response.setUpcomingSessions(upcoming);

            return response;
        }).toList();
    }

    @Override
    public List<CourseResponse> getAllByTeacherId(String teacherId) {
        return courseRepository.findAllByTeacherId(teacherId)
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }
}
