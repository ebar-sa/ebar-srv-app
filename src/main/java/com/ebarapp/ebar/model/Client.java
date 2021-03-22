package com.ebarapp.ebar.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="client")
public class Client extends User {
 
	private static final long serialVersionUID = -2215436288198955857L;
		
	@OneToOne(fetch = FetchType.LAZY)
    private BarTable table;
}
