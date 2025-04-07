package com.kirtesh.microsync.repository;

import com.kirtesh.microsync.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenAndTokenType(String token, String tokenType);
    void deleteByUsernameAndTokenType(String username, String tokenType);
}
