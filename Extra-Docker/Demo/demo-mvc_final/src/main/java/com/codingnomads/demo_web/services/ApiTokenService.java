package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.models.ApiToken;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.ApiTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiTokenService {
    private final ApiTokenRepository apiTokenRepository;
    private final JwtService jwtService;

    public ApiToken generate(User user) {
        return generate(user, java.time.Duration.ofHours(24));
    }

    public ApiToken generate(User user, java.time.Duration ttl) {
        String jwt = jwtService.generateToken(user.getUsername(), ttl);
        Instant now = Instant.now();
        // Rely on JWT's embedded expiration (already clamped in JwtService)
        Instant exp = jwtService.parseAndValidate(jwt).getExpiration().toInstant();
        ApiToken token = ApiToken.builder()
                .user(user)
                .token(jwt)
                .issuedAt(now)
                .expiresAt(exp)
                .revoked(false)
                .build();
        return apiTokenRepository.save(token);
    }

    public List<ApiToken> userTokens(Long userId) {
        return apiTokenRepository.findAllByUser_Id(userId);
    }

    public List<ApiToken> listAll() {
        return apiTokenRepository.findAll();
    }

    public void revoke(Long tokenId) {
        ApiToken token = apiTokenRepository.findById(tokenId).orElseThrow();
        token.setRevoked(true);
        token.setRevokedAt(Instant.now());
        apiTokenRepository.save(token);
    }

    public void delete(Long tokenId) {
        apiTokenRepository.deleteById(tokenId);
    }

    public boolean isActive(ApiToken token) {
        return !token.isRevoked() && token.getExpiresAt().isAfter(Instant.now());
    }

    public boolean isValidJwtAndActive(String jwt) {
        return apiTokenRepository.findByToken(jwt)
                .filter(this::isActive)
                .isPresent();
    }
}
