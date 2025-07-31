package com.example.skill_swap.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.skill_swap.Entities.CustomUserDetails;
import com.example.skill_swap.Entities.User;
import com.example.skill_swap.Repositories.UserRepository;
import com.example.skill_swap.dtoClasses.PasswordDto;
import com.example.skill_swap.dtoClasses.UserData;



@Service
public class UserService implements UserDetailsService{
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private UserRepository userRepository;
	
	//private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public User registerUser(User user) throws Exception {
		if(userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new Exception("This email id is already registered");
		}
		user.setPassword(encoder.encode(user.getPassword()));
		return userRepository.save(user);
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

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
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
    
    public void resetPassword(PasswordDto passwordDto) throws Exception {
        String token = passwordDto.getToken();
        String newPassword = passwordDto.getNewPassword();

        Optional<User> optionalUser = userRepository.findByResetToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Token has expired");
            }

            // Use password encoder to hash password
            user.setPassword(encoder.encode(newPassword));

            // Clear reset token and expiry
            user.setResetToken(null);
            user.setTokenExpiry(null);

            userRepository.save(user);
        } else {
            throw new RuntimeException("Invalid token");
        }
    }
    
    public String forgotPassword(String email) throws Exception {
    	Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setTokenExpiry(LocalDateTime.now().plusMinutes(30));
            userRepository.save(user);

            String link = "http://localhost:5500/reset-password.html?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Reset your Skill Swap Password");
            message.setText("Click the link to reset: " + link);

            mailSender.send(message);
            return "Mail sent to your registerd mail id";
            
        }
        else {
        	throw new RuntimeException("User not found");
        }
    }

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email).orElseThrow(() ->  new UsernameNotFoundException("user not found"));
		return new CustomUserDetails(user);
	}

	public Optional<User> getUserById(Long userId) {
		return userRepository.findById(userId);
	}
 
	

}
