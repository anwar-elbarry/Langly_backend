package com.langly.app.user.repository;

import com.langly.app.user.entity.Role;
import com.langly.app.user.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
}
