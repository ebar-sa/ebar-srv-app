package com.ebarapp.ebar.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;


public enum RolType {
	ROLE_CLIENTE(new SimpleGrantedAuthority("ROLE_CLIENTE")),
	ROLE_TRABAJADOR(new SimpleGrantedAuthority("ROLE_TRABAJADOR"));
	
	private final String name;
	private final SimpleGrantedAuthority authority;
	
	RolType(SimpleGrantedAuthority authority) {
		this.authority = authority;
		this.name = authority.getAuthority();
	}

	public SimpleGrantedAuthority getAuthority() {
		return authority;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
