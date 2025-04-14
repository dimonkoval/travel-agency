package com.epam.finaltask.dto;

import java.util.List;

import com.epam.finaltask.annotation.validation.ValidPassword;
import com.epam.finaltask.dto.groups.OAuthGroup;
import com.epam.finaltask.dto.groups.PasswordGroup;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {

	private String id;

	@NotBlank(message = "Username cannot be empty" , groups = {PasswordGroup.class, OAuthGroup.class})
	private String username;

	@NotBlank(message = "Password cannot be empty", groups = PasswordGroup.class)
	private String password;

	private Role role;

	private List<Voucher> vouchers;

	@NotBlank(message = "Last name cannot be empty", groups = PasswordGroup.class)
	private String phoneNumber;

	private Double balance;

	private boolean active;

	@Email(message = "Invalid email format", groups = {PasswordGroup.class, OAuthGroup.class})
	private String email;
}
