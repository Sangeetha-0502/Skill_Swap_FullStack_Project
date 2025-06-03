package com.example.skill_swap.skill_swap_project.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.skill_swap.skill_swap_project.Entities.Notification;
import com.example.skill_swap.skill_swap_project.Services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    
    @GetMapping("/get-notifications/{userId}")
    public List<Notification> getNotifications(@PathVariable Long userId) {
        try {
            return notificationService.getNotifications(userId);
        } catch (Exception e) {
            // You can log the error or throw a meaningful response
            throw new RuntimeException("Failed to fetch notifications: " + e.getMessage());
        }
    }

   
    @PutMapping("/mark-notification/{notificationId}")
    public Notification markNotificationAsRead(@PathVariable Long notificationId) throws Exception {
        return notificationService.markAsRead(notificationId);
    }
}

