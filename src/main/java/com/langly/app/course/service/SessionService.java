package com.langly.app.course.service;

import com.langly.app.course.web.dto.SessionRequest;
import com.langly.app.course.web.dto.SessionResponse;

import java.util.List;

public interface SessionService {

    SessionResponse create(SessionRequest request);

    SessionResponse getById(String id);

    List<SessionResponse> getAllByCourseId(String courseId);

    List<SessionResponse> getUpcomingByCourseId(String courseId);

    SessionResponse update(String id, SessionRequest request);

    void delete(String id);
}
