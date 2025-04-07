package com.kirtesh.microsync.repository;

import com.kirtesh.microsync.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findById(Long id);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
}

