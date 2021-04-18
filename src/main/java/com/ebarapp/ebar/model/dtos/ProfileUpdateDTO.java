package com.ebarapp.ebar.model.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ProfileUpdateDTO {

	@NotBlank
	private String username;
	
	@NotBlank
	@Email
	private String	email;
	
	@NotBlank
	private String	oldPassword;

	private String	password;

	private String	confirmPassword;
	
	public ProfileUpdateDTO() {
	}

	public ProfileUpdateDTO(String username,  String email, String oldPassword, String password, String confirmPassword) {
		this.username = username;
		this.email = email;
		this.oldPassword = oldPassword;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}
}
