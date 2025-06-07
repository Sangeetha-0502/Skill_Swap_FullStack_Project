package com.example.skill_swap.skill_swap_project.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skill_swap.skill_swap_project.Entities.ChatMessage;
import com.example.skill_swap.skill_swap_project.Repositories.ChatMessageRepository;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    // Fetch messages after a certain message ID or all if lastMessageId is null
    public List<ChatMessage> getMessages(Long chatId, Long lastMessageId) {
        if (lastMessageId == null) {
            return chatMessageRepository.findByChatIdOrderByTimestampAsc(chatId);
        }
        return chatMessageRepository.findByChatIdAndIdGreaterThanOrderByTimestampAsc(chatId, lastMessageId);
    }

    // Save a new chat message
    public ChatMessage saveMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }
}
