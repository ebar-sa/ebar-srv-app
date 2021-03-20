package com.ebarapp.ebar.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "usuario_login")
public class UsuarioLogin implements UserDetails {

	private static final long serialVersionUID = -2158042926789658742L;

	@NotNull
	@Id
	@Column(name = "username")
	private String username;

	@Column(name = "nombre")
	private String nombre;

	@Column(name = "apellidos")
	private String apellidos;

	@Column(name = "telefono")
	private String telefono;

	@Column(name = "email", unique = true)
	private String email;

	@NotNull
	@Column(name = "contrasenya")
	private String contrasenya;

	@Column(name = "dni")
	private String dni;
	
	@NotNull
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Rol> roles;
	
	public UsuarioLogin() {
	}
	
	public UsuarioLogin(String username, String email, String contrasenya) {
		this.email = email;
		this.username = username;
		this.contrasenya = contrasenya;
	}

	public Collection<? extends GrantedAuthority> getAuthorities(){
		return roles.stream().map(x->x.getRol().getAuthority()).collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		return contrasenya;
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
