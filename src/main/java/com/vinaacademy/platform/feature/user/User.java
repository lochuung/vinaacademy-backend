package com.vinaacademy.platform.feature.user;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.log.Log;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_slug", columnList = "slug")
})
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "avatarUrl")
    private String avatarUrl;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "description")
    private String description;

    @Column(name = "isColaborator")
    private boolean isColaborator;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "role")
    private String role;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "isUsing2FA")
    private boolean isUsing2FA;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // If a Log is deleted from the log
                                                                                   // list, it is also deleted from the
                                                                                   // database.
    private List<Log> logs;
}
