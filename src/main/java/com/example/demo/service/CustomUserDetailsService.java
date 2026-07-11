package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user and returns their profile with tenant details.
     * Throws an exception or returns null if authentication fails.
     */
    public User authenticate(String email, String rawPassword) {
        // 1. Look up the user by email in the central management DB
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        User user = userOptional.get();

        // 2. Cryptographically verify the raw password matches the database hash
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        // 3. Return the fully populated user (complete with their assigned tenantId)
        return user;
    }

	public User loadUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}
}