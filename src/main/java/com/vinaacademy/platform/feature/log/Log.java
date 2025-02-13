package com.vinaacademy.platform.feature.log;

import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.user.User;

import io.swagger.v3.core.util.Json;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "logs", indexes = {
        @Index(name = "idx_slug", columnList = "slug")
})
public class Log extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "action")
    private String action;

    @Column(name = "oldData")
    private Json oldData;

    @Column(name = "newData")
    private Json newData;

    @Column(name = "ipAddress")
    private String ipAddress;

    @Column(name = "userAgent")
    private String userAgent;
}
