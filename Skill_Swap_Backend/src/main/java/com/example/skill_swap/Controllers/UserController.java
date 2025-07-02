package com.example.skill_swap.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.skill_swap.Entities.User;
import com.example.skill_swap.Services.UserService;
import com.example.skill_swap.dtoClasses.LoginRequest;
import com.example.skill_swap.dtoClasses.PasswordDto;
import com.example.skill_swap.dtoClasses.UserData;
import com.example.skill_swap.dtoClasses.UserProfileUpdateRequest;


@CrossOrigin(origins = "http://localhost:5500")
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
	
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestParam String email) {
		try {
			String  msg = userservice.forgotPassword(email);
			return ResponseEntity.ok(msg);
		}
		catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody PasswordDto passwordDto) {
	    try {
	    	userservice.resetPassword(passwordDto);  // service method call
	        return ResponseEntity.ok("Password reset successfully!");
	    } catch (RuntimeException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());  // like token expired
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body("Something went wrong!");
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
	        return ResponseEntity.status(401).body("Invalid cre[dentials");
	    }
	}
	@PutMapping("/update-profile")
	public ResponseEntity<?> updateProfile(@RequestBody UserProfileUpdateRequest updateRequest) {
	    try {
	        User updatedUser = userservice.updateUserProfile(
	            updateRequest.getUserId(),
	            updateRequest.getName(),
	            updateRequest.getEmail(),
	            updateRequest.getBio()
	             // added name support
	        );

	        updatedUser.setPassword(null); // Hide password
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
	        return ResponseEntity.ok(profilePicUrl);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Failed to upload profile picture: " + e.getMessage());
	    }
	}
	
	
	@DeleteMapping("/delete-profile-picture/{userId}")
	public ResponseEntity<String> deleteProfilePicture(@PathVariable Long userId) {
	    try {
	        String message = userservice.deleteProfilePicture(userId);
	        return ResponseEntity.ok(message);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error deleting profile picture: " + e.getMessage());
	    }
	}

	@PutMapping("/add-linkedin/{userId}")
	public ResponseEntity<User> addLinkedIn(@PathVariable Long userId, @RequestParam String linkedInUrl) {
	    try {
	        User updatedUser = userservice.addLinkedIn(userId, linkedInUrl);
	        return ResponseEntity.ok(updatedUser);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}

	@PutMapping("/add-github/{userId}")
	public ResponseEntity<User> addGithub(@PathVariable Long userId, @RequestParam String githubUrl) {
	    try {
	        User updatedUser = userservice.addGithub(userId, githubUrl);
	        return ResponseEntity.ok(updatedUser);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}
	
	@PostMapping("/upload-certificate/{userId}")
    public ResponseEntity<String> uploadCertificate(@PathVariable Long userId,
                                                    @RequestParam("file") MultipartFile file) {
        try {
            String url = userservice.uploadCertificate(userId, file);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("delete-certificate/{userId}")
    public ResponseEntity<String> deleteCertificate(@PathVariable Long userId,
                                                    @RequestParam String certificateUrl) {
        try {
            userservice.deleteCertificateUrl(userId, certificateUrl);
            return ResponseEntity.ok("Certificate deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(e.getMessage());
        }
    }

    @GetMapping("user-certificates/{userId}/")
    public ResponseEntity<List<String>> getUserCertificates(@PathVariable Long userId) {
        Optional<User> userOpt = userservice.getUserById(userId);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get().getCertificateUrls());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    @GetMapping("/user-data/{userId}/")
    public ResponseEntity<?> getUserData(@PathVariable  Long userId){
    	try {
			UserData userdata = userservice.getUserProfile(userId);
			return ResponseEntity.ok(userdata);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
    }
}






