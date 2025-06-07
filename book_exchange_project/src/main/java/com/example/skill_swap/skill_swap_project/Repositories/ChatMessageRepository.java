package com.example.skill_swap.skill_swap_project.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.skill_swap.skill_swap_project.Entities.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatIdAndIdGreaterThanOrderByTimestampAsc(Long chatId, Long lastMessageId);
    List<ChatMessage> findByChatIdOrderByTimestampAsc(Long chatId);
}
