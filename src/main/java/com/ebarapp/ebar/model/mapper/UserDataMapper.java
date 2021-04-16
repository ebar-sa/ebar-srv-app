package com.ebarapp.ebar.model.mapper;

import java.util.Set;

import com.ebarapp.ebar.model.type.RoleType;

import lombok.Data;

@Data
public class UserDataMapper {

	private String username;
	private String	firstName;
	private String	lastName;
	private String	dni;
	private String	email;
	private String	phoneNumber;
	private String	password;
	private Set<RoleType> roles;
	
	public UserDataMapper() {
	}

	public UserDataMapper(String username, String firstName, String lastName, String dni, String email, String phoneNumber, String password,  Set<RoleType> roles) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dni = dni;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.roles = roles;
	}
}
