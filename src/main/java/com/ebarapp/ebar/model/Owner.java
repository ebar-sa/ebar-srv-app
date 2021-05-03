package com.ebarapp.ebar.model;


import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ebarapp.ebar.model.mapper.UserDataMapper;
import com.ebarapp.ebar.model.type.RoleType;

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

    private String braintreeMerchantId;

    private String braintreePublicKey;

    private String braintreePrivateKey;

    public Owner () {
    }

    public Owner (UserDataMapper userData) {
        super(userData.getUsername(), userData.getFirstName(), userData.getLastName(), userData.getDni(), userData.getEmail(), userData.getPhoneNumber(), userData.getPassword(), userData.getRoles());
    }
    
    public Owner (String username, String firstName, String lastName, String dni, String email, String phoneNumber, String password, Set<RoleType> roles, Set<Bar> bar) {
        super(username, firstName, lastName, dni, email, phoneNumber, password, roles);
        this.bar = bar;
    }

}
