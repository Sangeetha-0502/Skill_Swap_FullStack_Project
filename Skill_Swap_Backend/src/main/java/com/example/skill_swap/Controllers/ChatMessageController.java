package com.example.skill_swap.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.skill_swap.Entities.ChatMessage;
import com.example.skill_swap.Entities.Message;
import com.example.skill_swap.Services.ChatMessageService;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

	

	    @Autowired
	    private ChatMessageService chatService;

	    @PostMapping("/send")
	    public ResponseEntity<?> sendMessage(@RequestBody ChatMessage chatRequest) {
	        try {
	            if (chatRequest.getSenderId() == null || chatRequest.getReceiverId() == null || chatRequest.getContent() == null || chatRequest.getContent().isBlank()) {
	                return ResponseEntity.badRequest().body("Invalid chat request: sender, receiver, and content must be provided.");
	            }

	            Message savedMessage = chatService.sendMessage(
	                chatRequest.getSenderId(),
	                chatRequest.getReceiverId(),
	                chatRequest.getContent()
	            );

	            return ResponseEntity.ok(savedMessage);

	        } catch (Exception e) {
	            e.printStackTrace(); // optional: use a logger
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Failed to send message: " + e.getMessage());
	        }
	    }


	    @GetMapping("/history")
	    public List<Message> getChatHistory(@RequestParam Long userId1, @RequestParam Long userId2) {
	        return chatService.getMessages(userId1, userId2);
	    }
	


}
