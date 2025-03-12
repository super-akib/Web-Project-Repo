package com.md.taskmanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.md.taskmanagementsystem.model.User;
import com.md.taskmanagementsystem.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    // Show registration page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";  
    }

    // Handle user registration with auto-login
    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult result, Model model) {
        // Check for validation errors
        if (result.hasErrors()) {
            return "auth/register"; 
        }     

        try {
            // Check if username already exists
            if (userService.existsByUsername(user.getUsername())) {
                result.rejectValue("username", "error.user", "Username is already taken");
                return "auth/register";
            }

            // Encode password before saving
            String rawPassword = user.getPassword();
            user.setPassword(passwordEncoder.encode(rawPassword));

            // Save user
            userService.registerUser(user);

            // Authenticate user automatically after registration
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), rawPassword)
            );

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Redirect to home page after successful registration
            return "redirect:/home"; 
            
        } catch (DataIntegrityViolationException e) {
            // Handle unique constraint violations (email)
            result.rejectValue("email", "error.user", "Email is already registered");
            return "auth/register";
        } catch (Exception e) {
            // Handle other unexpected errors
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            return "auth/register";
        }
    }

    // Show login page
    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login"; 
    }
}
