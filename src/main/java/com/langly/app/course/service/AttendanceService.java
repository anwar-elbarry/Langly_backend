package com.langly.app.course.service;

import com.langly.app.course.entity.enums.AttendanceStatus;
import com.langly.app.course.web.dto.AttendanceResponse;
import com.langly.app.course.web.dto.CourseAttendanceSummaryResponse;
import com.langly.app.course.web.dto.StudentAttendanceSummaryResponse;

import java.util.List;

/**
 * US05 : Service de gestion de la présence.
 */
public interface AttendanceService {

    /** Le prof consulte la liste de présence d'une session. */
    List<AttendanceResponse> getAttendanceBySession(String sessionId);

    /** Le prof marque manuellement la présence d'un étudiant. */
    AttendanceResponse markManual(String sessionId, String studentId, AttendanceStatus status);

    /** Retourne tous les inscrits avec leur statut de présence pour une session. */
    List<AttendanceResponse> getFullAttendanceBySession(String sessionId);

    /** Résumé global de présence d'un étudiant. */
    StudentAttendanceSummaryResponse getStudentGlobalSummary(String userId);

    /** Résumé de présence d'un étudiant par cours. */
    List<CourseAttendanceSummaryResponse> getStudentCourseSummaries(String userId);
}
