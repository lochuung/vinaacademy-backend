package com.vinaacademy.platform.feature.password_reset;

import java.time.LocalDateTime;

import com.vinaacademy.platform.feature.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "password_reset")
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "expiredAt")
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
