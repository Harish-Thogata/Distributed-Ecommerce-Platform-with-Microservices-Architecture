package com.userservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.common.dto.CustomerIdDTO;
import com.userservice.client.OrderServiceClient;
import com.userservice.dto.LoginDTO;
import com.userservice.dto.ResponseDTO;
import com.userservice.dto.UserDTO;
import com.userservice.entity.User;
import com.userservice.repository.UserRepository;

@Service
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JWTService jwtService;

	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private JavaMailSender mailSender;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
	
	@Autowired
    private OrderServiceClient orderServiceClient; // FeignClient
	
	// @Autowired
	// private KafkaTemplate<String, String> kafkaTemplate;
	
	// private static final String TOPIC = "customer-login-topic";

    @Transactional
	public ResponseEntity<String> registerUser(UserDTO userDTO) {
	    // Check if email already exists
	    if (userRepository.findByEmailId(userDTO.getEmailId()).isPresent()) {
	    	logger.warn("User with email {} already exists", userDTO.getEmailId());
	        return new ResponseEntity<>("User with this email already exists", HttpStatus.BAD_REQUEST);
	    }

	    // Encode the user's password before saving
	    String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

	    // Create a new User entity
	    User user = new User();
	    user.setCustomerName(userDTO.getCustomerName());
	    user.setEmailId(userDTO.getEmailId());
	    user.setPassword(encodedPassword);
	    user.setPhoneNumber(userDTO.getPhoneNumber());

	    // Save the user
	    User savedUser = userRepository.save(user);
	    logger.info("User registered successfully with ID: {}", savedUser.getCustomerId());

	    // Send email
	    sendWelcomeEmail(savedUser.getEmailId(), savedUser.getCustomerId());

	    return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
	}

	
	private void sendWelcomeEmail(String email, Long customerId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Welcome to Our Service");
        message.setText("Your account has been created with ID: " + customerId);
        
        mailSender.send(message);
        logger.info("Welcome email sent to: {}", email);
    }
	
	public ResponseEntity<ResponseDTO> loginUser(LoginDTO loginDTO) {
	    try {
	    	// Authenticate user
	        Authentication authentication = authManager.authenticate(
	                new UsernamePasswordAuthenticationToken(loginDTO.getEmailId(), loginDTO.getPassword()));

	        if (authentication.isAuthenticated()) {
	        	// Generate JWT token
	            String token = jwtService.generateToken(loginDTO.getEmailId());

	            // Retrieve user to get customerId
	            
	            /*User user = userRepository.findByEmailId(loginDTO.getEmailId()).orElse(null);
	            if (user != null) {
	                String customerId = String.valueOf(user.getCustomerId());

	                // Send customerId to Kafka topic
	                kafkaTemplate.send(TOPIC, customerId);
	                logger.info("Sent customerId to Kafka: {}", customerId);
	            }*/
	            
	            // Fetch user details
                User user = userRepository.findByEmailId(loginDTO.getEmailId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
                
                // Send customerId to Order Service via FeignClient
                try {
                    orderServiceClient.sendCustomerId(new CustomerIdDTO(user.getCustomerId(), user.getEmailId()));
                    logger.info("Sent customerId={} to Order Service", user.getCustomerId());
                } catch (Exception e) {
                    logger.error("Failed to send customerId to Order Service: {}", e.getMessage());
                }

	            // Return ResponseDTO with success message and token
	            return new ResponseEntity<>(new ResponseDTO("Login successfull", token), HttpStatus.OK);
	        }
	    } catch (Exception ex) {
	    	logger.error("Login failed for user: {}", loginDTO.getEmailId(), ex);
	    }

	    return new ResponseEntity<>(new ResponseDTO("Invalid credentials", null), HttpStatus.UNAUTHORIZED);
	}
}
