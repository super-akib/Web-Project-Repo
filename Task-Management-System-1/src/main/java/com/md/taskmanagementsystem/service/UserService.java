package com.md.taskmanagementsystem.service;

import java.util.Optional;

import com.md.taskmanagementsystem.model.User;

public interface UserService {

    // Register a new user
    User registerUser(User user);

    // Get user by username
    Optional<User> getUserByUsername(String username);

    // Check if a username exists
    boolean existsByUsername(String username);
    
    // edit user details 
    public User updateUser(Long userId, User updatedUser);
    //delete user account
    public void deleteUser(Long userId);
}
