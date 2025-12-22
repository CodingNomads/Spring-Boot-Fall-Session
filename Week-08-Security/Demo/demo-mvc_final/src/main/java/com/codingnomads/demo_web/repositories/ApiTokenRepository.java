package com.codingnomads.demo_web.repositories;

import com.codingnomads.demo_web.models.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ApiTokenRepository extends JpaRepository<ApiToken, Long> {
    Optional<ApiToken> findByToken(String token);
    List<ApiToken> findAllByUser_Id(Long userId);
    List<ApiToken> findAllByRevokedFalseAndExpiresAtAfter(Instant now);
}
