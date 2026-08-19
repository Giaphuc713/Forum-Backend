package com.backend.Forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.Forum.entity.Role;
import com.backend.Forum.entity.Role.ERole;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}