package com.smartcare.repository;

import com.smartcare.model.Notification;
import com.smartcare.model.NotificationType;
import com.smartcare.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);
    
    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient = :recipient AND n.isRead = false")
    Long countUnreadByRecipient(@Param("recipient") User recipient);
    
    List<Notification> findByRecipientAndTypeOrderByCreatedAtDesc(User recipient, NotificationType type);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.isRead = false AND n.priority >= :priority ORDER BY n.priority DESC, n.createdAt DESC")
    List<Notification> findUnreadHighPriorityNotifications(@Param("recipient") User recipient, @Param("priority") Integer priority);
    
    @Query("SELECT n FROM Notification n WHERE n.expiresAt IS NOT NULL AND n.expiresAt <= :now")
    List<Notification> findExpiredNotifications(@Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.isEmailSent = false AND n.type IN :emailTypes")
    List<Notification> findPendingEmailNotifications(@Param("emailTypes") List<NotificationType> emailTypes);
    
    @Query("SELECT n FROM Notification n WHERE n.isPushSent = false AND n.type IN :pushTypes")
    List<Notification> findPendingPushNotifications(@Param("pushTypes") List<NotificationType> pushTypes);
    
    @Query("DELETE FROM Notification n WHERE n.recipient = :recipient AND n.isRead = true AND n.createdAt < :cutoffDate")
    void deleteOldReadNotifications(@Param("recipient") User recipient, @Param("cutoffDate") LocalDateTime cutoffDate);
} 