package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.dto.notification.NotificationResponse;
import com.smartcare.security.UserPrincipal;
import com.smartcare.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/my-notifications")
    @Operation(summary = "Get user notifications", description = "Retrieve paginated list of user's notifications")
    public ResponseEntity<?> getMyNotifications(
            @AuthenticationPrincipal UserPrincipal currentUser,
            Pageable pageable) {
        try {
            Page<NotificationResponse> notifications = notificationService.getUserNotifications(currentUser.getId(), pageable);
            return ResponseEntity.ok(new ApiResponse(true, "Notifications retrieved successfully", notifications));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Retrieve all unread notifications for the user")
    public ResponseEntity<?> getUnreadNotifications(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            List<NotificationResponse> notifications = notificationService.getUnreadNotifications(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Unread notifications retrieved", notifications));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications")
    public ResponseEntity<?> getUnreadNotificationCount(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Long count = notificationService.getUnreadNotificationCount(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Unread count retrieved", count));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{notificationId}/mark-read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    public ResponseEntity<?> markNotificationAsRead(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(currentUser.getId(), notificationId);
            return ResponseEntity.ok(new ApiResponse(true, "Notification marked as read"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/mark-all-read")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read for the user")
    public ResponseEntity<?> markAllNotificationsAsRead(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            notificationService.markAllAsRead(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "All notifications marked as read"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification", description = "Delete a specific notification")
    public ResponseEntity<?> deleteNotification(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(currentUser.getId(), notificationId);
            return ResponseEntity.ok(new ApiResponse(true, "Notification deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/create")
    @Operation(summary = "Create notification", description = "Create a custom notification (admin only)")
    public ResponseEntity<?> createNotification(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam Long recipientId,
            @RequestParam String type,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam(required = false, defaultValue = "1") Integer priority,
            @RequestParam(required = false) String actionUrl) {
        try {
            NotificationResponse notification = notificationService.createNotification(
                    recipientId,
                    com.smartcare.model.NotificationType.valueOf(type.toUpperCase()),
                    title,
                    message,
                    priority,
                    actionUrl
            );
            return ResponseEntity.ok(new ApiResponse(true, "Notification created successfully", notification));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
} 