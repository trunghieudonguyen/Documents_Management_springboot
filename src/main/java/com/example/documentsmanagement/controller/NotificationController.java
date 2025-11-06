package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.model.Notification;
import com.example.documentsmanagement.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Lấy các thông báo 1 tháng gần đây
    @GetMapping("/recent")
    public List<Notification> getRecentNotifications() {
        return notificationService.getRecentNotifications();
    }

    @PutMapping("/{id}")
    public Notification updateNotification(
            @PathVariable Long id,
            @RequestBody Notification updatedNotification
    ) {
        return notificationService.updateNotification(id, updatedNotification);
    }
}
