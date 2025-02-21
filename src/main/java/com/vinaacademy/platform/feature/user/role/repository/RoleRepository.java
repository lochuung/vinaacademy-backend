package com.vinaacademy.platform.feature.user.role.repository;

import com.vinaacademy.platform.feature.user.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByCode(String name);
}
