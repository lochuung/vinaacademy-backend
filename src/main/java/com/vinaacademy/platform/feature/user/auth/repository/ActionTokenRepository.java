package com.vinaacademy.platform.feature.user.auth.repository;

import com.vinaacademy.platform.feature.user.auth.entity.ActionToken;
import com.vinaacademy.platform.feature.user.auth.enums.ActionTokenType;
import com.vinaacademy.platform.feature.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.*;
import java.util.Optional;

public interface ActionTokenRepository extends JpaRepository<ActionToken, Long> {
    Optional<ActionToken> findByTokenAndType(String token, ActionTokenType type);

    Optional<ActionToken> findByUserAndType(User user, ActionTokenType actionTokenType);
}
