package com.langly.app.Authority.repository;

import com.langly.app.Authority.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission,String> {
}
