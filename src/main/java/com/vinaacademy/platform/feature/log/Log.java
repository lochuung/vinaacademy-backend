package com.vinaacademy.platform.feature.log;

import com.fasterxml.jackson.databind.JsonNode;
import com.vinaacademy.platform.feature.common.constant.DatabaseConstants;
import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "logs")
public class Log extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "action")
    private String action;

    @Column(name = "old_data", columnDefinition = DatabaseConstants.JSON_TYPE)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode oldData;

    @Column(name = "new_data", columnDefinition = DatabaseConstants.JSON_TYPE)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode newData;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;
}
