
package com.ebarapp.ebar.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import com.ebarapp.ebar.model.dtos.ItemMenuDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item_menu")
public class ItemMenu extends BaseEntity {

	public ItemMenu(ItemMenuDTO itemDTO) {
		this.name = itemDTO.getName();
		this.description = itemDTO.getDescription();
		this.category = itemDTO.getCategory();
		this.image = itemDTO.getImage();
		this.rationType = itemDTO.getRationType();
		this.price = itemDTO.getPrice();
	}

	public ItemMenu() {
	
	}

	@NotNull
	@Column(name = "name")
	private String		name;

	@Column(name = "description")
	private String		description;
	
	@NotNull
	@Column(name = "ration_type")
	private String rationType;

	@NotNull
	@Column(name = "price")
	private Double		price;

	@NotNull
	@Column(name = "category")
	private String category;

	@OneToOne(cascade = CascadeType.ALL)
	private DBImage		image;

}
