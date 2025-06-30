package com.example.skill_swap.skill_swap_project.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.skill_swap.skill_swap_project.Entities.Message;
import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Repositories.MessageRepository;
import com.example.skill_swap.skill_swap_project.Repositories.UserRepository;

@Service
public class ChatMessageService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private UserRepository userRepository;

	public Message sendMessage(Long senderId, Long receiverId, String content) {
		User sender = userRepository.findById(senderId).orElseThrow();
		User receiver = userRepository.findById(receiverId).orElseThrow();

		Message message = new Message();
		message.setSender(sender);
		message.setReceiver(receiver);
		message.setContent(content);
		message.setTimestamp(LocalDateTime.now());
		sendNewMessageNotification(receiver.getEmail(), sender.getName());
		return messageRepository.save(message);
	}

	public void sendNewMessageNotification(String toEmail, String senderName) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("You've got a new message!");
		message.setText("Hi, you’ve received a new message from " + senderName + "! Log in to Skill Swap to reply.");
		mailSender.send(message);
	}

	public List<Message> getMessages(Long userId1, Long userId2) {
		return messageRepository.getChatHistory(userId1, userId2);
	}
}
