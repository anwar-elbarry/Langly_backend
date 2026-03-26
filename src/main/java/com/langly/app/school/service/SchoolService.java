package com.langly.app.school.service;

import com.langly.app.school.web.dto.SchoolRequest;
import com.langly.app.school.web.dto.SchoolResponse;
import com.langly.app.school.web.dto.SchoolUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SchoolService {

    SchoolResponse create(SchoolRequest request);

    SchoolResponse getById(String id);

    List<SchoolResponse> getByName(String name);

    List<SchoolResponse> getAll();

    SchoolResponse update(String id, SchoolUpdateRequest request);

    SchoolResponse uploadLogo(String id, MultipartFile file);

    void delete(String id);
}
