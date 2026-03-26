package com.langly.app.user.service.user;

import com.langly.app.Authority.entity.Role;
import com.langly.app.email.EmailPreview;
import com.langly.app.email.EmailService;
import com.langly.app.exception.AlreadyExistsException;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.school.entity.School;
import com.langly.app.school.repository.SchoolRepository;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.user.entity.User;
import com.langly.app.user.enums.UserStatus;
import com.langly.app.user.repository.RoleRepository;
import com.langly.app.user.repository.UserRepository;
import com.langly.app.user.web.dto.request.UpdatePasswordRequest;
import com.langly.app.user.web.dto.request.UserRequest;
import com.langly.app.user.web.dto.request.UserUpdateRequest;
import com.langly.app.user.web.dto.response.UserResponse;
import com.langly.app.user.web.mapper.UserMapper;
import com.langly.app.shared.util.FileStorageService;
import com.langly.app.shared.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordGenerator passwordGenerator;
    private final FileStorageService fileStorageService;
    @Value("${app.base-url}")
    private String appBaseUrl;

    @Override
    public UserResponse create(UserRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }

        // Récupérer le rôle
        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.getRoleName()));

        // Récupérer l'école
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School", request.getSchoolId()));

        // Générer un mot de passe aléatoire
        String rawPassword = passwordGenerator.generate();

        // Créer l'utilisateur
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setSchool(school);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        // Si le rôle est STUDENT, créer automatiquement le profil étudiant
        if ("STUDENT".equalsIgnoreCase(role.getName())) {
            Student student = new Student();
            student.setUser(savedUser);
            studentRepository.save(student);
        }

        // V2 — envoie l'email ou retourne un aperçu en dev (app.mail.enabled=false)
        EmailPreview emailPreview = emailService.sendInvitationEmailWithPreview(
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                role.getName(),
                user.getEmail(),
                rawPassword,
                school.getName()
        );

        UserResponse response = userMapper.toResponse(savedUser);
        response.setEmailPreview(emailPreview); // null en production, rempli en dev
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllBySchoolId(String schoolId) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", schoolId);
        }

        return userRepository.findAllBySchoolId(schoolId).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllByRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", roleName));

        return userRepository.findAllByRole(role).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllBySchoolAndRole(String schoolId, String roleName) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", schoolId);
        }
        return userRepository.findAllBySchoolIdAndRoleName(schoolId, roleName).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse update(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        userRepository.findByEmail(request.getEmail())
                .filter(existingUser -> !existingUser.getId().equals(id))
                .ifPresent(existingUser -> {
                    throw new AlreadyExistsException("Un utilisateur avec cet email existe déjà");
                });

        // Mettre à jour les champs
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setProfile(request.getProfile());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void delete(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userRepository.delete(user);
    }

    @Override
    public void activate(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void suspend(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setStatus(UserStatus.SUSPENDED);
        userRepository.save(user);
    }

    @Override
    public void updatePassword(String id, UpdatePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        // Vérifier que le mot de passe actuel est correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect");
        }

        // Vérifier que le nouveau mot de passe et la confirmation correspondent
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe et la confirmation ne correspondent pas");
        }

        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserResponse uploadProfileImage(String id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        // Store the file and get the generated filename
        String filename = fileStorageService.store(file);
        
        // Build the URL to serve the file
        String base = appBaseUrl != null ? appBaseUrl.replaceAll("/$", "") : "";
        String fileUrl = base + "/api/v1/files/" + filename;
        
        user.setProfile(fileUrl);
        User updatedUser = userRepository.save(user);
        
        return userMapper.toResponse(updatedUser);
    }

}
