package com.example.skill_swap.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skill_swap.Entities.Notification;
import com.example.skill_swap.Entities.User;
import com.example.skill_swap.Repositories.NotificationRepository;
import com.example.skill_swap.Repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendNotification(Long recipientId, String message) throws Exception {
        User user = userRepository.findById(recipientId)
                .orElseThrow(() -> new Exception("User not found"));

        Notification notification = new Notification();
        notification.setRecipient(user);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        return notificationRepository.findByRecipient(user);
    }

    public Notification markAsRead(Long notificationId) throws Exception {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exception("Notification not found"));

        notification.setRead(true);
       return  notificationRepository.save(notification);
    }
}

