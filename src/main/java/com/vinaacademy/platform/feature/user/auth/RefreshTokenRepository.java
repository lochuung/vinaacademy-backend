package com.vinaacademy.platform.feature.user.auth;

import com.vinaacademy.platform.feature.user.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.expireTime < :now")
    void deleteExpiredTokens(LocalDateTime now);

}
