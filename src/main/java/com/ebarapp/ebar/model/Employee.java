package com.ebarapp.ebar.model;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="employee")
public class Employee extends User {

	private static final long serialVersionUID = 8632504696241787808L;
	@JsonIgnore
    @ManyToOne
    private Bar bar;

}
