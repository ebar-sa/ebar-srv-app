package com.ebarapp.ebar.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ebarapp.ebar.model.type.RoleType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User implements UserDetails {

	private static final long serialVersionUID = -2158042926789658742L;

	@NotNull
	@Id
	@Column(name = "username")
	protected String username;

	@NotBlank
	@Column(name = "first_name")
	protected String	firstName;

	@NotBlank
	@Column(name = "last_name")
	protected String	lastName;

	@Column(name = "dni", unique = true)
	protected String	dni;

	@NotBlank
	@Email
	@Column(name = "email", unique = true)
	protected String	email;

	@Column(name = "phone_number")
	@Pattern(regexp = "^[+]*[(]?[0-9]{1,4}[)]?[-\\s\\./0-9]*$", message = "Must be a valid phone number")
	protected String	phoneNumber;
	
	@NotBlank
	protected String	password;
	
	@NotNull
	@ElementCollection(targetClass=RoleType.class, fetch=FetchType.EAGER)
	@Column(name = "role")
	private Set<RoleType> roles;
	
	public User() {
	}
	
	public User(String username, String email, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public Collection<? extends GrantedAuthority> getAuthorities(){
		return roles.stream().map(RoleType::getAuthority).collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
