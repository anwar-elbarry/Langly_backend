package com.langly.app.Authority.service.role;

import com.langly.app.Authority.entity.Permission;
import com.langly.app.Authority.exception.RoleNotFoundException;
import com.langly.app.Authority.repository.PermissionRepository;
import com.langly.app.Authority.web.dto.request.RoleRequest;
import com.langly.app.Authority.web.dto.response.RoleResponse;
import com.langly.app.Authority.web.mapper.RoleMapper;
import com.langly.app.Authority.entity.Role;
import com.langly.app.exception.AlreadyExistsException;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleResponse create(RoleRequest request) {
        Role role = roleMapper.toEntity(request);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponse(savedRole);
    }

    @Override
    public RoleResponse getById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
        return roleMapper.toResponse(role);
    }

    @Override
    public RoleResponse getByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + name));
        return roleMapper.toResponse(role);
    }

    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleResponse update(String id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
        roleMapper.updateEntity(request, role);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponse(savedRole);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
        roleRepository.delete(role);
    }

    @Override
    public RoleResponse assignPermissions(String roleId, List<String> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));

        permissionIds.forEach(permissionId -> {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission", permissionId));

            if (role.getPermissions().stream()
                    .anyMatch(p -> p.getId().equals(permissionId))) {
                throw new AlreadyExistsException(permission.getName());
            }

            role.getPermissions().add(permission);
        });

        Role updatedRole = roleRepository.save(role);
        return roleMapper.toResponse(updatedRole);
    }

    @Override
    public RoleResponse takePermission(String roleId, List<String> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));

        permissionIds.forEach(permissionId -> {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission", permissionId));

            role.getPermissions().remove(permission);
        });
        Role saved = roleRepository.save(role);
        return roleMapper.toResponse(saved);
    }
}
