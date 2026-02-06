package com.langly.app.user.repository;

import com.langly.app.Authority.entity.Role;
import com.langly.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(Role role);
    List<User> findAllBySchoolId(String schoolId);
}
