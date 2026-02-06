package com.langly.app.school.service;

import com.langly.app.school.web.dto.SchoolRequest;
import com.langly.app.school.web.dto.SchoolResponse;
import com.langly.app.school.web.dto.SchoolUpdateRequest;

import java.util.List;

public interface SchoolService {

    SchoolResponse create(SchoolRequest request);

    SchoolResponse getById(String id);

    SchoolResponse getBySubDomain(String subDomain);

    List<SchoolResponse> getByName(String name);

    List<SchoolResponse> getAll();

    SchoolResponse update(String id, SchoolUpdateRequest request);

    void delete(String id);
}
