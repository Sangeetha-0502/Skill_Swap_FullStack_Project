package com.example.skill_swap.JWTClasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import com.example.skill_swap.Entities.User;
import com.example.skill_swap.Services.UserService;
import com.example.skill_swap.dtoClasses.LoginRequest;

@RestController
@RequestMapping("/api/user")
public class JwtAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtill jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            User user = userService.getUserByEmail(loginRequest.getEmail());

            String jwt = jwtUtil.generateToken(user.getEmail(), user.getId());

            return ResponseEntity.ok(
                new JwtResponse(
                    user.getId(),
                    user.getName(),
                    user.getProfilePictureUrl(),
                    jwt
                )
            );

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/auth/user-register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);

            String jwt = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());

            savedUser.setPassword(null);

            return ResponseEntity.ok(
                new JwtResponse(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getProfilePictureUrl(),
                    jwt
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
