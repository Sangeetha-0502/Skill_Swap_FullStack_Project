package com.example.skill_swap.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.skill_swap.Entities.Notification;
import com.example.skill_swap.Entities.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(User recipient);
}
