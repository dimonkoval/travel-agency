package com.epam.finaltask.dto;

import java.util.List;

import com.epam.finaltask.annotation.validation.ValidPassword;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {

	private String id;

	@NotBlank(message = "Username cannot be empty")
	private String username;

	@NotBlank(message = "Password cannot be empty")
	@ValidPassword
	private String password;

	@NotNull(message = "Role cannot be null")
	private Role role;

	private List<Voucher> vouchers;

	@NotBlank(message = "Last name cannot be empty")
	private String phoneNumber;

	private Double balance;

	private boolean active;

	@Email(message = "Invalid email format")
	private String email;
}
