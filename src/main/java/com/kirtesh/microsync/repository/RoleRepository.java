package com.kirtesh.microsync.repository;

import com.kirtesh.microsync.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String userRole);
}
