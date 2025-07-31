package com.example.skill_swap.JWTClasses;

public class JwtResponse {

	private Long userId;
	private String userName;
	private String profilePictureUrl;
	private String token;

	public Long getUserId() {
		return userId;
	}

	public JwtResponse(Long userId, String userName, String profilePictureUrl, String token) {
		this.userId = userId;
		this.userName = userName;
		this.profilePictureUrl = profilePictureUrl;
		this.token = token;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
