package com.ebarapp.ebar.configuration.security.payload.response;

import java.util.List;

public class LoginResponse {
	
	private String token;
	private String type = "Bearer";
	private String username;
	private String dni;
	private String email;
	private String firstName;
	private String lastName;
	private List<String> roles;
	
	public LoginResponse(String token, String username, String dni, String email, String firstName,
			String lastName, List<String> roles) {
		super();
		this.token = token;
		this.username = username;
		this.dni = dni;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.roles = roles;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}
}
