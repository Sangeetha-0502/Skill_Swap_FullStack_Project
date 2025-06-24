package com.example.skill_swap.skill_swap_project.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Repositories.UserRepository;
import com.example.skill_swap.skill_swap_project.dtoClasses.UserData;



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
	
	public User updateUserProfile(Long userId, String name,String email, String bio) throws Exception {
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new RuntimeException("User not found"));
	    
	    if (name != null && !name.isBlank()) {
	        user.setName(name);
	    }

	    if (email != null && !email.isBlank()) {
	        user.setEmail(email);
	    }

	    if (bio != null && !bio.isBlank()) {
	        user.setBio(bio);
	    }
	    return userRepository.save(user);
	}

	public User addLinkedIn(Long userId, String linkedInUrl) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (linkedInUrl != null && !linkedInUrl.trim().isEmpty()) {
            user.setLinkedInUrl(linkedInUrl.trim());
        }

        return userRepository.save(user);
    }
	 public User addGithub(Long userId, String githubUrl) throws Exception {
	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new Exception("User not found"));

	        if (githubUrl != null && !githubUrl.trim().isEmpty()) {
	            user.setGithubUrl(githubUrl.trim());
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

	
	public String deleteProfilePicture(Long userId) throws IOException {
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    String profilePicPath = user.getProfilePictureUrl(); // This is like /uploads/1_xyz.jpg

	    if (profilePicPath != null && !profilePicPath.isEmpty()) {
	        Path filePath = Paths.get("." + profilePicPath); // Because it's a relative path
	        Files.deleteIfExists(filePath); // Delete file from filesystem
	    }

	    user.setProfilePictureUrl(null); // Remove path from DB
	    userRepository.save(user);

	    return "Profile picture deleted successfully.";
	}

    public String uploadCertificate(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String uploadDir = "uploads/certificates/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = userId + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String certificateUrl = "/" + uploadDir + fileName;
        user.getCertificateUrls().add(certificateUrl);
        userRepository.save(user);

        return certificateUrl;
    }

    public String deleteCertificateUrl(Long userId, String certificateUrl) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<String> urls = user.getCertificateUrls();
            boolean removed = urls.removeIf(url -> url.equals(certificateUrl));

            if (!removed) {
                throw new RuntimeException("Certificate URL not found");
            }

            user.setCertificateUrls(urls);
            userRepository.save(user);
            return "Certificate deleted successfully!";
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }


    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    public UserData getUserProfile(Long userId) throws Exception {
    	User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException ("User not found"));
    	
    	UserData userdata = new UserData();
    	userdata.setName(user.getName());
    	userdata.setEmail(user.getEmail());
    	userdata.setBio(user.getBio());
    	userdata.setLinkedInUrl(user.getLinkedInUrl());
    	userdata.setGithubUrl(user.getGithubUrl());
    	userdata.setProfilePictureUrl(user.getProfilePictureUrl());
    	userdata.setCertificateUrls(user.getCertificateUrls());
    	
    	return userdata;
    }




}
