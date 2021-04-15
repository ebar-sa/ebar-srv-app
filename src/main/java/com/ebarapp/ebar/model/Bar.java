
package com.ebarapp.ebar.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bar")
public class Bar extends BaseEntity {

	@NotEmpty
	@Column(name = "name")
	private String			name;

	@NotEmpty
	@Column(name = "description")
	private String			description;

	@NotEmpty
	@Column(name="contact")
	private String contact;

	@NotEmpty
	@Column(name = "location")
	private String			location;

	@Column(name = "opening_time")
	private Date			openingTime;

	@Column(name = "closing_time")
	private Date			closingTime;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<DBImage>	images;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Menu menu = new Menu();
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<BarTable>	barTables;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Voting>		votings;

	@ManyToOne(fetch = FetchType.LAZY)
	private Owner			owner;

	@OneToMany(fetch = FetchType.LAZY)
	private Set<Employee> employees;

	public void addVoting(Voting newVoting) { getVotings().add(newVoting); }

	public void deleteVoting(Voting oldVoting) { getVotings().remove(oldVoting); }

}

