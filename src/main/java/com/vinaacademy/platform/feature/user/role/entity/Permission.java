package com.vinaacademy.platform.feature.user.role.entity;

import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "permission")
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
}
