package com.langly.app.course.controller;

import com.langly.app.course.service.SessionService;
import com.langly.app.course.web.dto.SessionRequest;
import com.langly.app.course.web.dto.SessionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "Gestion des sessions de cours")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    @Operation(summary = "Créer une session")
    public ResponseEntity<SessionResponse> create(@Valid @RequestBody SessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER','STUDENT')")
    @Operation(summary = "Obtenir une session par ID")
    public ResponseEntity<SessionResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(sessionService.getById(id));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER','STUDENT')")
    @Operation(summary = "Lister toutes les sessions d'un cours")
    public ResponseEntity<List<SessionResponse>> getAllByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(sessionService.getAllByCourseId(courseId));
    }

    @GetMapping("/course/{courseId}/upcoming")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER','STUDENT')")
    @Operation(summary = "Lister les sessions à venir d'un cours")
    public ResponseEntity<List<SessionResponse>> getUpcomingByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(sessionService.getUpcomingByCourseId(courseId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    @Operation(summary = "Mettre à jour une session")
    public ResponseEntity<SessionResponse> update(@PathVariable String id, @Valid @RequestBody SessionRequest request) {
        return ResponseEntity.ok(sessionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    @Operation(summary = "Supprimer une session")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
