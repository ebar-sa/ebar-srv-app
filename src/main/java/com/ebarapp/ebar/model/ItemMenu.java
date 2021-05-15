
package com.ebarapp.ebar.model;

import javax.persistence.*;

import javax.validation.constraints.NotNull;

import com.ebarapp.ebar.model.dtos.CreateItemMenuDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "item_menu")
public class ItemMenu extends BaseEntity {

	public ItemMenu(CreateItemMenuDTO itemDTO) {
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

	@OneToMany(fetch = FetchType.LAZY)
	private Set<Review> reviews;

	public void addReview(Review review) {
		getReviews().add(review);
	}

}
