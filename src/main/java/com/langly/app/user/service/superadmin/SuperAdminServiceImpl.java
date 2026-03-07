package com.langly.app.user.service.superadmin;

import com.langly.app.exception.UserNotFoundException;
import com.langly.app.Authority.entity.Role;
import com.langly.app.user.entity.User;
import com.langly.app.user.enums.UserRole;
import com.langly.app.user.enums.UserStatus;
import com.langly.app.user.repository.RoleRepository;
import com.langly.app.user.repository.UserRepository;
import com.langly.app.user.web.dto.request.SuperAdminRequest;
import com.langly.app.user.web.dto.response.UserResponse;
import com.langly.app.user.web.mapper.SuperAdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SuperAdminMapper superAdminMapper;
    private  final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(SuperAdminRequest request) {
        String newPassword = passwordEncoder.encode(request.getPassword());
        Role superAdminRole = roleRepository.findByName(UserRole.SUPER_ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("Super Admin role not found in database"));

        User user = superAdminMapper.toEntity(request);
        user.setRole(superAdminRole);
        user.setStatus(UserStatus.ACTIVE);
        user.setSchool(null);
        user.setPassword(newPassword);

        User savedUser = userRepository.save(user);
        return superAdminMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getById(String id) {
        User user = findSuperAdminById(id);
        return superAdminMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAll() {
        Role superAdminRole = roleRepository.findByName(UserRole.SUPER_ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("Super Admin role not found in database"));

        return userRepository.findAllByRole(superAdminRole).stream()
                .map(superAdminMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse update(String id, SuperAdminRequest request) {
        User user = findSuperAdminById(id);
        superAdminMapper.updateEntity(request, user);
        User savedUser = userRepository.save(user);
        return superAdminMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public void delete(String id) {
        User user = findSuperAdminById(id);
        userRepository.delete(user);
    }

    private User findSuperAdminById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Super Admin not found with id: " + id));

        Role superAdminRole = roleRepository.findByName(UserRole.SUPER_ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("Super Admin role not found in database"));

        if (user.getRole() == null || !user.getRole().getId().equals(superAdminRole.getId())) {
            throw new UserNotFoundException("User is not a Super Admin: " + id);
        }

        return user;
    }
}
