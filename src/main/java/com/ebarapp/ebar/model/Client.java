package com.ebarapp.ebar.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ebarapp.ebar.model.mapper.UserDataMapper;

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

	  public Client(User user, BarTable barTable) {
		  super();
		  this.username = user.username;
		  this.dni = user.dni;
		  this.email = user.email;
		  this.firstName = user.firstName;
		  this.lastName = user.lastName;
		  this.password = user.password;
		  this.phoneNumber = user.phoneNumber;
		  this.table = barTable;
	  }
	
	  public Client() {
		  super();
	  }	

    
    public Client (UserDataMapper userData) {
        super(userData.getUsername(), userData.getFirstName(), userData.getLastName(), userData.getDni(), userData.getEmail(), userData.getPhoneNumber(), userData.getPassword(), userData.getRoles());
    }

}
