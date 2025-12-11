package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.security.AuthUtil;
import com.example.demo.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthUtil authUtil;

    public NotificationController(NotificationService notificationService,
                                  AuthUtil authUtil) {
        this.notificationService = notificationService;
        this.authUtil = authUtil;
    }

    // ============================================================
    //                  GET MY NOTIFICATIONS
    // ============================================================
    @GetMapping("/me")
    public ResponseEntity<?> getMyNotifications() {

        String userId = authUtil.getCurrentUserId();

        List<Notification> list = notificationService.getForUser(userId);
        return ResponseEntity.ok(list);
    }

    // ============================================================
    //                  MARK MY NOTIFICATIONS READ
    // ============================================================
    @PostMapping("/me/read-all")
    public ResponseEntity<?> markMyNotificationsRead() {

        String userId = authUtil.getCurrentUserId();

        notificationService.markAllRead(userId);
        return ResponseEntity.ok("Notifications marked as read");
    }
}
