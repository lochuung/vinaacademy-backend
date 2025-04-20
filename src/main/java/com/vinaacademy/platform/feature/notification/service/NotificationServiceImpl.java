package com.vinaacademy.platform.feature.notification.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.exception.UnauthorizedException;
import com.vinaacademy.platform.feature.notification.dto.NotificationCreateDTO;
import com.vinaacademy.platform.feature.notification.dto.NotificationDTO;
import com.vinaacademy.platform.feature.notification.entity.Notification;
import com.vinaacademy.platform.feature.notification.enums.NotificationType;
import com.vinaacademy.platform.feature.notification.mapper.NotificationMapper;
import com.vinaacademy.platform.feature.notification.repository.NotificationRepository;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.auth.helpers.SecurityHelper;
import com.vinaacademy.platform.feature.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final SecurityHelper securityHelper;

	@Override
	public NotificationDTO createNotification(NotificationCreateDTO dto) {
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy user"));
		Notification notification = NotificationMapper.INSTANCE.toEntity(dto);
		notification.setUser(user);
		notification.setCreatedAt(LocalDateTime.now());
		notification.setIsDeleted(false);
		notification.setIsRead(false);
		notification = notificationRepository.save(notification);
		return NotificationMapper.INSTANCE.toDTO(notification);
	}

	// Lấy danh sách các notifications từ user lấy từ Auth backend thông
	// qua tokenAccess
	@Override
	public Page<NotificationDTO> getUserNotificationsPaginated(Boolean read, NotificationType type, Pageable pageable) {
		User user = findUser();
		Page<Notification> page;

		if (type != null && read != null) {
			page = notificationRepository.findByUserAndIsDeletedFalseAndTypeAndIsRead(user, type, read, pageable);
		} else if (type != null) {
			page = notificationRepository.findByUserAndIsDeletedFalseAndType(user, type, pageable);
		} else if (read != null) {
			page = notificationRepository.findByUserAndIsDeletedFalseAndIsRead(user, read, pageable);
		} else {
			page = notificationRepository.findByUserAndIsDeletedFalse(user, pageable);
		}

		return page.map(NotificationMapper.INSTANCE::toDTO);

	}
	
	//đánh dấu đã đọc cho noti
	@Override
	public void markAsRead(UUID notificationId) {
		Notification notification = findNotification(notificationId);
		User user = findUser();
		if (!notification.getUser().getId().equals(user.getId()))
			throw UnauthorizedException.message("Xác thực thất bại vui lòng kiểm tra lại");
		notification.setIsRead(true);
		notification.setReadAt(LocalDateTime.now());
		notificationRepository.save(notification);
	}
	
	//chuyển noti sang status bị xóa
	@Override
	public void deleteNotification(UUID notificationId) {
		Notification notification = findNotification(notificationId);
		User user = findUser();
		if (!notification.getUser().getId().equals(user.getId()))
			throw UnauthorizedException.message("Xác thực thất bại vui lòng kiểm tra lại");
		notification.setIsDeleted(true);
		notificationRepository.save(notification);
	}

	public Notification findNotification(UUID id) {
		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy thông báo"));
		return notification;
	}
	
	// đánh dấu đã đọc tất cả noti
	@Override
	public void markReadAll() {
		User user = findUser();
		notificationRepository.markUnreadAndUndeletedAsRead(user);

	}

	public User findUser() {
		User user = securityHelper.getCurrentUser();
		if (user == null)
			throw UnauthorizedException.message("Xác thực thất bại vui lòng kiểm tra lại");
		return user;
	}

}