package com.example.skill_swap.skill_swap_project.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.skill_swap.skill_swap_project.Entities.ChatMessage;
import com.example.skill_swap.skill_swap_project.Services.ChatMessageService;

public class ChatMessageController {
	@RestController
	@RequestMapping("/api/chat")
	public class ChatController {

	    @Autowired
	    private ChatMessageService chatmessageservice;

	    @GetMapping("/{chatId}/messages")
	    public List<ChatMessage> getMessages(
	        @PathVariable Long chatId,
	        @RequestParam(required = false) Long afterMessageId) {

	        return chatmessageservice.getMessages(chatId, afterMessageId);
	    }

	    @PostMapping("/{chatId}/messages")
	    public ChatMessage sendMessage(
	        @PathVariable Long chatId,
	        @RequestBody ChatMessage message) {

	        message.setChatId(chatId);
	        return chatmessageservice.saveMessage(message);
	    }
	}


}
