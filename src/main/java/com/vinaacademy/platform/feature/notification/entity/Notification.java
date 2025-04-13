package com.vinaacademy.platform.feature.notification.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.vinaacademy.platform.feature.course.enums.CourseStatus;
import com.vinaacademy.platform.feature.notification.enums.NotificationType;
import com.vinaacademy.platform.feature.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "content")
	private String content;
	
	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;
	
	@Column(name = "is_read", nullable = false)
	private Boolean isRead;
	
	@Column(name = "read_at", nullable = false)
	private LocalDateTime readAt;
	
	@ManyToOne
    @JoinColumn(name = "recipient")
	private User user;
	
	@Column(name = "target_url")
	private String targetUrl;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationType type = NotificationType.SYSTEM;

}	
