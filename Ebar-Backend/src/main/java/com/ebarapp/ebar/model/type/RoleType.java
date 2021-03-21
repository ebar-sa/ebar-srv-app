package com.ebarapp.ebar.model.type;

import org.springframework.security.core.authority.SimpleGrantedAuthority;


public enum RoleType {
	ROLE_CLIENT(new SimpleGrantedAuthority("ROLE_CLIENT")),
	ROLE_OWNER(new SimpleGrantedAuthority("ROLE_OWNER")),
	ROLE_EMPLOYEE(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
	
	private final String name;
	private final SimpleGrantedAuthority authority;
	
	RoleType(SimpleGrantedAuthority authority) {
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
