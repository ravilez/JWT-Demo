package com.example.demo.controller;


import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtils;
    
    @Autowired
    CustomUserDetailsService userService;

    @PostMapping("/signin")
    public String authenticateUser(@RequestBody User request) {

    	
    	// 1. Authenticate user credentials against your DB
        User user = userService.authenticate(request.getEmail(), request.getPassword());
        
        // Credentials are already validated by userService.authenticate.
        String token = jwtUtils.generateToken(user.getUsername(), user.getEmail(), user.getTenantId());
        System.out.println("Generated JWT Token: " + token); // Debugging line
        return token;
    }

    @PostMapping("/signup")
    public String registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Error: Username is already taken!";
        }

        User newUser = new User(
                null,
                user.getUsername(),
                user.getEmail(),
                encoder.encode(user.getPassword()),
                user.getTenantId()
        );

        userRepository.save(newUser);
        return "User registered successfully!";
    }
}