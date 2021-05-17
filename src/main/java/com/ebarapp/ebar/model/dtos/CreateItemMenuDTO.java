package com.ebarapp.ebar.model.dtos;

import com.ebarapp.ebar.model.DBImage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateItemMenuDTO {

	private String name;
	
	private String description;
	
	private String rationType;
	
	private Double price;
	
	private String category;
	
	private DBImage image;
	
}
