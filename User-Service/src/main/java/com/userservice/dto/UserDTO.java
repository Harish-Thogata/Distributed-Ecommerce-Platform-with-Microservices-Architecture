package com.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	
	@NotBlank
    private String customerName;

    @Email
    @NotBlank
    private String emailId;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank
    private String phoneNumber;

}
