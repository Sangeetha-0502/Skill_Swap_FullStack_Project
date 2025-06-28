package com.example.skill_swap.skill_swap_project.Entities;

public class ChatMessage {

	    private Long senderId;
	    private Long receiverId;
	    private String content;

	    // 👉 Getters & Setters
	    public Long getSenderId() {
	        return senderId;
	    }

	    public void setSenderId(Long senderId) {
	        this.senderId = senderId;
	    }

	    public Long getReceiverId() {
	        return receiverId;
	    }

	    public void setReceiverId(Long receiverId) {
	        this.receiverId = receiverId;
	    }

	    public String getContent() {
	        return content;
	    }

	    public void setContent(String content) {
	        this.content = content;
	    }
	
}

