package com.ebarapp.ebar.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "menu")
public class Menu extends BaseEntity {
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ItemMenu> items;
	
	public Set<Category> getCategories() {
		Set<Category> categories = new HashSet<>();
		items.forEach(x->categories.add(x.getCategory()));
		return categories;
	}
}
