package com.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.userservice.config.CustomUserDetails;
import com.userservice.entity.User;
import com.userservice.exception.UserNotFoundException;
import com.userservice.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String emailId) throws UserNotFoundException {
		
		User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + emailId));
		
        return new CustomUserDetails(user);
	}
}
