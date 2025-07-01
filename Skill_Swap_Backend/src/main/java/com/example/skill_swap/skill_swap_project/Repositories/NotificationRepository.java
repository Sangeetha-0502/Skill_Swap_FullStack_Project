package com.example.skill_swap.skill_swap_project.Repositories;

import com.example.skill_swap.skill_swap_project.Entities.Notification;
import com.example.skill_swap.skill_swap_project.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(User recipient);
}
