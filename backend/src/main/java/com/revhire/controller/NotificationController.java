package com.revhire.controller;

import com.revhire.dto.NotificationDto;
import com.revhire.entity.Notification;
import com.revhire.security.UserDetailsImpl;
import com.revhire.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getId())));
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<Map<String, String>> markAllAsRead(@AuthenticationPrincipal UserDetailsImpl user) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserDetailsImpl user) {
        notificationService.markAsRead(id, user.getId());
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }
}
