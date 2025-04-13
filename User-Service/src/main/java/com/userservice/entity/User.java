package com.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers", uniqueConstraints = {
	    @UniqueConstraint(columnNames = "email_id"),
	    @UniqueConstraint(columnNames = "phone_number")
	})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long customerId;

	 @NotNull
	 @Column(nullable = false)
	 private String customerName;

	 @NotNull
	 @Column(nullable = false, unique = true)
	 private String emailId;

	 @NotNull
	 @Column(nullable = false)
	 private String password;
	 
	 @NotNull
	 @Column(nullable = false, unique = true)
	 private String phoneNumber;

}