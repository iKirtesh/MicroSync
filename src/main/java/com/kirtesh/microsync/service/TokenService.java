package com.kirtesh.microsync.service;

import com.kirtesh.microsync.component.TokenGenerator;
import com.kirtesh.microsync.model.Token;
import com.kirtesh.microsync.repository.TokenRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token createToken(String username, String tokenType) {
        String token = TokenGenerator.generateToken(); // 6-digit OTP
        Token newToken = Token.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusMinutes(10))// OTP valid for 10 minutes
                .username(username)
                .tokenType(tokenType)
                .build();
        tokenRepository.deleteByUsernameAndTokenType(username, tokenType); // Remove any previous OTPs
        return tokenRepository.save(newToken);
    }

    public boolean validateToken(String token, String username, String tokenType) {
        return tokenRepository.findByTokenAndTokenType(token, tokenType)
                .map(storedToken -> storedToken.getUsername().equals(username) &&
                        storedToken.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    public void deleteToken(String username, String tokenType) {
        tokenRepository.deleteByUsernameAndTokenType(username, tokenType);
    }
}
