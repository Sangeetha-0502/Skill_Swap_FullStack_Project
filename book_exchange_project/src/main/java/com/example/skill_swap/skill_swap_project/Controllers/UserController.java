package com.example.skill_swap.skill_swap_project.Controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Services.UserService;
import com.example.skill_swap.skill_swap_project.dtoClasses.LoginRequest;
import com.example.skill_swap.skill_swap_project.dtoClasses.UserProfileUpdateRequest;



@RestController
@RequestMapping("api/user")
public class UserController {
	
	@Autowired
	private UserService userservice;
	
	public UserService getUserservice() {
		return userservice;
	}

	public void setUserservice(UserService userservice) {
		this.userservice = userservice;
	}

	@PostMapping("/user-register")
	public ResponseEntity<?> RegisterUser(@RequestBody User user){//request body annotation is responsible for converting the json data into a java object 
		try {
			User saveduser = userservice.registerUser(user);
			saveduser.setPassword(null);
			return ResponseEntity.ok(saveduser);
		} catch (Exception e) {
			 return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/user-login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
	    Optional<User> optionalUser = userservice.loginUser(
	        loginRequest.getEmail(),
	        loginRequest.getPassword()
	    );

	    if (optionalUser.isPresent()) {
	        User user = optionalUser.get();
	        user.setPassword(null);
	        return ResponseEntity.ok(user);
	    } else {
	        return ResponseEntity.status(401).body("Invalid credentials");
	    }
	}
	@PutMapping("/update-profile")
	public ResponseEntity<?> updateProfile(@RequestBody UserProfileUpdateRequest updateRequest) {
	    try {
	        User updatedUser = userservice.updateUserProfile(
	            updateRequest.getUserId(),
	            updateRequest.getEmail(),
	            updateRequest.getBio()
	        );
	        updatedUser.setPassword(null);
	        return ResponseEntity.ok("Profile Updated Successfully");
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}
	@PostMapping("/upload-profile-picture/{userId}")
	public ResponseEntity<String> uploadProfilePicture(@PathVariable Long userId,
	                                                   @RequestParam("file") MultipartFile file) {
	    try {
	        String profilePicUrl = userservice.uploadProfilePicture(userId, file);
	        return ResponseEntity.ok("Profile picture uploaded successfully: " + profilePicUrl);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Failed to upload profile picture: " + e.getMessage());
	    }
	}



}

