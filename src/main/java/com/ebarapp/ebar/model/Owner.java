package com.ebarapp.ebar.model;


import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="owner")
public class Owner extends User {

	private static final long serialVersionUID = 8632504696241787808L;
	
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Bar> bar;

}
