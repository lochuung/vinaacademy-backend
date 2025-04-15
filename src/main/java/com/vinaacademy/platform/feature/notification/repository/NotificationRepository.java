package com.vinaacademy.platform.feature.notification.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.vinaacademy.platform.feature.notification.entity.Notification;
import com.vinaacademy.platform.feature.notification.enums.NotificationType;
import com.vinaacademy.platform.feature.user.entity.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserAndIsDeletedFalse(User user, Pageable pageable);
    Page<Notification> findByUserAndIsDeletedFalseAndType(User user, NotificationType type, Pageable pageable);
    Page<Notification> findByUserAndIsDeletedFalseAndIsRead(User user, Boolean isRead, Pageable pageable);
    Page<Notification> findByUserAndIsDeletedFalseAndTypeAndIsRead(User user, NotificationType type, Boolean isRead, Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user = :user AND n.isDeleted = false AND n.isRead = false")
    int markUnreadAndUndeletedAsRead(User user);


}