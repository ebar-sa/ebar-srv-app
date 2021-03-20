package com.ebarapp.ebar.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "rol")
public class Rol {
	
	@Id
	@NotNull
	@Enumerated(EnumType.STRING)
	private RolType rol;

	public Rol() {
	}
	
	public Rol(RolType rol) {
		this.rol = rol;
	}
}
