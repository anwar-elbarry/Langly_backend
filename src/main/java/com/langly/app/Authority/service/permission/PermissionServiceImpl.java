package com.langly.app.Authority.service.permission;

import com.langly.app.Authority.entity.Permission;
import com.langly.app.Authority.repository.PermissionRepository;
import com.langly.app.Authority.web.dto.response.PermissionResponse;
import com.langly.app.Authority.web.mapper.PermissionMapper;
import com.langly.app.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResponse create(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.toResponse(savedPermission);
    }

    @Override
    public List<PermissionResponse> createMulti(List<String> names) {
        List<Permission> permissions = names.stream()
                .map(name -> {
                    Permission permission = new Permission();
                    permission.setName(name);
                    return permission;
                })
                .toList();
        List<Permission> savedPermissions = permissionRepository.saveAll(permissions);
        return savedPermissions.stream()
                .map(permissionMapper::toResponse)
                .toList();
    }

    @Override
    public PermissionResponse getById(String id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission" , id));
        return permissionMapper.toResponse(permission);
    }


    @Override
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toResponse).toList();
    }

    @Override
    public void delete(String id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission", id);
        }
        permissionRepository.deleteById(id);
    }
}
