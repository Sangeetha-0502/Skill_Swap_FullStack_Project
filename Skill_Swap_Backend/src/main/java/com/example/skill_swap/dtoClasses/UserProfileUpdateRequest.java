package com.example.skill_swap.dtoClasses;

public class UserProfileUpdateRequest {
           private Long userId;
           private String email;
           private String bio;
           private String name; 
           
           
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getBio() {
			return bio;
		}
		public void setBio(String bio) {
			this.bio = bio;
		}
           
           
}
