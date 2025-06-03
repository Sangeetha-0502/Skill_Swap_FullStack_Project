package com.example.skill_swap.skill_swap_project.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path; 
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Repositories.UserRepository;



@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	//private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public User registerUser(User user) throws Exception {
		if(userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new Exception("This email id is already registered");
		}
		return userRepository.save(user);
	}
	
	public Optional<User> loginUser(String email, String password){
		 Optional<User> userOpt = userRepository.findByEmail(email);
		 
		 if(userOpt.isPresent()) {
			 User user = userOpt.get();
			 if(password.equals(user.getPassword())) {
				 return Optional.of(user);
			 }
		 }
		 return Optional.empty();
	}
	
	public User updateUserProfile(Long userId, String newEmail, String newBio) throws Exception {
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new Exception("User not found"));

	    if (newEmail != null && !newEmail.trim().isEmpty()) {
	        user.setEmail(newEmail.trim());
	    }

	    if (newBio != null && !newBio.trim().isEmpty()) {
	        user.setBio(newBio.trim());
	    }

	    return userRepository.save(user);
	}
	public String uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
	    User user = userRepository.findById(userId)
	                  .orElseThrow(() -> new RuntimeException("User not found"));

	    // Create uploads directory if not exists
	    String uploadDir = "uploads/";
	    Path uploadPath = Paths.get(uploadDir);

	    if (!Files.exists(uploadPath)) {
	        Files.createDirectories(uploadPath);
	    }

	    // Create a unique file name like userId_filename.jpg
	    String fileName = userId + "_" + file.getOriginalFilename();
	    Path filePath = uploadPath.resolve(fileName);

	    // Copy the file content to the uploads folder
	    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	    // Save URL to database (you can also save absolute path or serve it from backend later)
	    String profilePicUrl = "/" + uploadDir + fileName;
	    user.setProfilePictureUrl(profilePicUrl);
	    userRepository.save(user);

	    return profilePicUrl;
	}



}
