package com.langly.app.school.service;

import com.langly.app.course.entity.enums.SchoolStatus;
import com.langly.app.finance.repository.BillingSettingRepository;
import com.langly.app.school.entity.School;
import com.langly.app.school.exception.SchoolNotFoundException;
import com.langly.app.school.repository.SchoolRepository;
import com.langly.app.school.web.dto.SchoolRequest;
import com.langly.app.school.web.dto.SchoolResponse;
import com.langly.app.school.web.dto.SchoolUpdateRequest;
import com.langly.app.school.web.mapper.SchoolMapper;
import com.langly.app.shared.util.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolServiceImplTest {

    @Mock private SchoolRepository schoolRepository;
    @Mock private SchoolMapper schoolMapper;
    @Mock private BillingSettingRepository billingSettingRepository;
    @Mock private FileStorageService fileStorageService;

    @InjectMocks private SchoolServiceImpl schoolService;

    private School school;
    private SchoolResponse schoolResponse;

    @BeforeEach
    void setUp() {
        school = new School();
        school.setId("school-1");
        school.setName("Langly Academy");
        school.setCity("Casablanca");
        school.setCountry("Morocco");
        school.setAddress("123 Rue de langue");
        school.setStatus(SchoolStatus.ACTIVE);

        schoolResponse = new SchoolResponse();
        schoolResponse.setId("school-1");
        schoolResponse.setName("Langly Academy");
        schoolResponse.setCity("Casablanca");
        schoolResponse.setCountry("Morocco");
        schoolResponse.setStatus("ACTIVE");
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("should create school with PENDING status and default billing settings")
        void shouldCreateSchool() {
            SchoolRequest request = SchoolRequest.builder()
                    .name("New School")
                    .city("Rabat")
                    .country("Morocco")
                    .build();

            when(schoolMapper.toEntity(request)).thenReturn(school);
            when(schoolRepository.save(any(School.class))).thenReturn(school);
            when(schoolMapper.toResponse(school)).thenReturn(schoolResponse);

            SchoolResponse result = schoolService.create(request);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Langly Academy");
            verify(schoolRepository).save(any(School.class));
            verify(billingSettingRepository).save(any());
            assertThat(school.getStatus()).isEqualTo(SchoolStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("should return school when found")
        void shouldReturnSchool() {
            when(schoolRepository.findById("school-1")).thenReturn(Optional.of(school));
            when(schoolMapper.toResponse(school)).thenReturn(schoolResponse);

            SchoolResponse result = schoolService.getById("school-1");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("school-1");
        }

        @Test
        @DisplayName("should throw SchoolNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(schoolRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> schoolService.getById("unknown"))
                    .isInstanceOf(SchoolNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getByName()")
    class GetByName {

        @Test
        @DisplayName("should return schools matching name")
        void shouldReturnSchools() {
            when(schoolRepository.findByName("Langly")).thenReturn(List.of(school));
            when(schoolMapper.toResponse(school)).thenReturn(schoolResponse);

            List<SchoolResponse> result = schoolService.getByName("Langly");

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getAll()")
    class GetAll {

        @Test
        @DisplayName("should return all schools")
        void shouldReturnAllSchools() {
            when(schoolRepository.findAll()).thenReturn(List.of(school));
            when(schoolMapper.toResponse(school)).thenReturn(schoolResponse);

            List<SchoolResponse> result = schoolService.getAll();

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should return empty list when no schools")
        void shouldReturnEmptyList() {
            when(schoolRepository.findAll()).thenReturn(List.of());

            List<SchoolResponse> result = schoolService.getAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("should update school successfully")
        void shouldUpdateSchool() {
            SchoolUpdateRequest request = SchoolUpdateRequest.builder()
                    .name("Updated School")
                    .city("Fes")
                    .build();

            when(schoolRepository.findById("school-1")).thenReturn(Optional.of(school));
            when(schoolRepository.save(any(School.class))).thenReturn(school);
            when(schoolMapper.toResponse(school)).thenReturn(schoolResponse);

            SchoolResponse result = schoolService.update("school-1", request);

            assertThat(result).isNotNull();
            verify(schoolMapper).updateEntity(school, request);
            verify(schoolRepository).save(school);
        }

        @Test
        @DisplayName("should throw SchoolNotFoundException when school not found")
        void shouldThrowWhenNotFound() {
            SchoolUpdateRequest request = SchoolUpdateRequest.builder().name("X").build();
            when(schoolRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> schoolService.update("unknown", request))
                    .isInstanceOf(SchoolNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("uploadLogo()")
    class UploadLogo {

        @Test
        @DisplayName("should upload logo and update school")
        void shouldUploadLogo() {
            MultipartFile mockFile = mock(MultipartFile.class);
            when(schoolRepository.findById("school-1")).thenReturn(Optional.of(school));
            when(fileStorageService.store(mockFile)).thenReturn("logo-file.png");
            when(schoolRepository.save(any(School.class))).thenReturn(school);
            when(schoolMapper.toResponse(school)).thenReturn(schoolResponse);

            SchoolResponse result = schoolService.uploadLogo("school-1", mockFile);

            assertThat(result).isNotNull();
            assertThat(school.getLogo()).contains("logo-file.png");
            verify(fileStorageService).store(mockFile);
        }

        @Test
        @DisplayName("should throw SchoolNotFoundException when school not found")
        void shouldThrowWhenNotFound() {
            MultipartFile mockFile = mock(MultipartFile.class);
            when(schoolRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> schoolService.uploadLogo("unknown", mockFile))
                    .isInstanceOf(SchoolNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("should delete school successfully")
        void shouldDeleteSchool() {
            when(schoolRepository.existsById("school-1")).thenReturn(true);

            schoolService.delete("school-1");

            verify(schoolRepository).deleteById("school-1");
        }

        @Test
        @DisplayName("should throw SchoolNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(schoolRepository.existsById("unknown")).thenReturn(false);

            assertThatThrownBy(() -> schoolService.delete("unknown"))
                    .isInstanceOf(SchoolNotFoundException.class);
        }
    }
}
