package com.ebarapp.ebar.model.dtos;

import javax.persistence.Column;

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
