package com.md.taskmanagementsystem.service.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.md.taskmanagementsystem.exception.UserNotFoundException;
import com.md.taskmanagementsystem.model.User;
import com.md.taskmanagementsystem.repository.UserRepository;
import com.md.taskmanagementsystem.service.EmailService;
import com.md.taskmanagementsystem.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User savedUser = userRepository.save(user);
        sendWelcomeEmail(user);        	
        return savedUser;
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public User updateUser(Long userId, User updatedUser) {
        return userRepository.findById(userId)
            .map(existingUser -> {
                existingUser.setFirstName(updatedUser.getFirstName());
                existingUser.setLastName(updatedUser.getLastName());
                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                existingUser.setEmail(updatedUser.getEmail());
                return userRepository.save(existingUser);
            }).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }
    
    private void sendWelcomeEmail(User user) {
        String email = user.getEmail();
        String subject = "Welcome to Task Management System!";

        Map<String, Object> model = Map.of(
            "userName", user.getUsername()
        );

        emailService.sendEmail(email, subject, "email/welcome_email", model);
    }
}
