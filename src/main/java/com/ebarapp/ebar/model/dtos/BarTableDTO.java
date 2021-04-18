package com.ebarapp.ebar.model.dtos;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.Client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarTableDTO {

	@Column(name = "name")
	private String	name;


	@Column(name = "seats")
	private Integer	seats;

}
