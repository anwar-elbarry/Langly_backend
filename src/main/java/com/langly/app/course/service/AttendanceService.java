package com.langly.app.course.service;

import com.langly.app.course.web.dto.AttendanceResponse;
import com.langly.app.course.web.dto.QrCodeResponse;

import java.util.List;

/**
 * US05 : Service de gestion de la présence.
 */
public interface AttendanceService {
    /** Le prof génère un QR token pour une session. */
    QrCodeResponse generateQr(String sessionId);

    /** L'étudiant scanne le QR et marque sa présence. */
    AttendanceResponse markPresent(String userId, String sessionId, String qrToken);

    /** Le prof consulte la liste de présence d'une session. */
    List<AttendanceResponse> getAttendanceBySession(String sessionId);
}
