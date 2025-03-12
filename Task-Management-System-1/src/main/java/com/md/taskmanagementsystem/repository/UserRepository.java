package com.md.taskmanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.md.taskmanagementsystem.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
	// find user based on username or email 
	Optional<User> findByUsername(String username);
	
	//Check user exists or not 
	boolean  existsByUsername(String username);
}
