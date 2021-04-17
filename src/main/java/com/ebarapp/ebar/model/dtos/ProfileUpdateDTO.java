package com.ebarapp.ebar.model.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ProfileUpdateDTO {

	@NotBlank
	private String username;
	@NotBlank
	@Email
	private String	email;
	@NotBlank
	@Size(min = 6, max = 40)
	private String	oldPassword;
	@NotBlank
    @Size(min = 6, max = 40)
	private String	password;
	@NotBlank
    @Size(min = 6, max = 40)
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
