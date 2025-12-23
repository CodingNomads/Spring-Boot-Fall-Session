package com.codingnomads.demo_web.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class JwtService {

    private final SecretKey key;
    private final long defaultTtlHours;
    private static final long MAX_TTL_HOURS = 24L; // hard upper bound

    public JwtService(@Value("${jwt.secret:dev-secret-change-me-dev-secret-change-me}") String secret,
                      @Value("${jwt.ttlHours:24}") long defaultTtlHours) {
        log.debug("Initializing JwtService with default TTL: {} hours", defaultTtlHours);
        // jjwt requires sufficient key length for HS256
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.defaultTtlHours = defaultTtlHours;
    }

    public String generateToken(String username) {
        return generateTokenHours(username, defaultTtlHours);
    }

    /**
     * Generate a token with the provided TTL in hours. Will be clamped to max 24h and min 1 minute.
     */
    public String generateTokenHours(String username, long ttlHours) {
        log.debug("Generating token for user: {} with requested TTL: {} hours", username, ttlHours);
        long hours = Math.max(0L, ttlHours);
        if (hours > MAX_TTL_HOURS) hours = MAX_TTL_HOURS;
        // convert to minutes (preserve at least 1 minute)
        long minutes = Math.max(1L, hours * 60L);
        return generateToken(username, java.time.Duration.ofMinutes(minutes));
    }

    /**
     * Generate a token with the provided TTL duration. Will be clamped to max 24h and min 1 minute.
     */
    public String generateToken(String username, java.time.Duration ttl) {
        log.debug("Generating token for user: {} with duration: {}", username, ttl);
        java.time.Duration effective = ttl == null ? java.time.Duration.ofHours(defaultTtlHours) : ttl;
        if (effective.isNegative() || effective.isZero()) {
            effective = java.time.Duration.ofMinutes(1);
        }
        java.time.Duration max = java.time.Duration.ofHours(MAX_TTL_HOURS);
        if (effective.compareTo(max) > 0) {
            effective = max;
        }
        Instant now = Instant.now();
        Instant exp = now.plus(effective);
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(Map.of("typ", "api", "coding", "nomads"))
                .signWith(key)
                .compact();
        log.info("Successfully generated JWT for user: {}", username);
        return token;
    }

    public Claims parseAndValidate(String jwt) {
        log.debug("Parsing and validating JWT");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            log.debug("JWT validated successfully for subject: {}", claims.getSubject());
            return claims;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            throw e;
        }
    }

    public String extractUsername(String jwt) {
        return parseAndValidate(jwt).getSubject();
    }
}
