
package com.ebarapp.ebar.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import com.ebarapp.ebar.model.dtos.BarTableDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({
	"bar", "bill", "client"
})
@Table(name = "bar_table")
public class BarTable extends BaseEntity {
	
	public  BarTable() {}
	
	public BarTable(BarTableDTO barTableDTO) {
		this.name  = barTableDTO.getName();
		this.seats = barTableDTO.getSeats();
	}

	@NotNull
	@Column(name = "name")
	private String	name;

	@NotNull
	@Column(name = "token")
	private String	token;

	@NotNull
	@Column(name = "free")
	private boolean	free;

	@NotNull
	@Column(name = "seats")
	private Integer	seats;

	@NotNull
	@ManyToOne
	private Bar		bar;

	@OneToOne(fetch = FetchType.LAZY)
	private Bill	bill;

	
	@OneToOne(fetch = FetchType.LAZY)
	private Client	client;
	
	

}
