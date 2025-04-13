package com.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.dto.LoginDTO;
import com.userservice.dto.ResponseDTO;
import com.userservice.dto.UserDTO;
import com.userservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Service", description = "User management API")
public class UserController {
	
	private final UserService service;

    // Constructor injection recommended over field injection
    public UserController(UserService service) {
        this.service = service;
    }
	
	 //@Autowired
	 //private UserService service;
	 
	 @Operation(summary = "Register a new user", description = "Registers a user and returns a success message.")
	 @ApiResponses(value = {
	            @ApiResponse(responseCode = "201", description = "Successfully registered", 
	                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
	            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", 
	                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
	 })
	 @PostMapping("/register")
	 public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
	     return service.registerUser(userDTO); // Directly return the ResponseEntity from the service method
	 }
	 

	 @Operation(summary = "User login", description = "Authenticates a user and returns user details with a JWT token.")
	 @ApiResponses(value = {
	            @ApiResponse(responseCode = "200", description = "Successfully logged in", 
	                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),
	            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", 
	                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
	 })
	 @PostMapping("/login")
	 public ResponseEntity<ResponseDTO> loginUser(@RequestBody LoginDTO loginDTO) {
	     return service.loginUser(loginDTO);
	 }
}