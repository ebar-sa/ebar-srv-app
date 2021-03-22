package com.ebarapp.ebar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category extends BaseEntity {

	@NotNull
	@Column(name = "name")
	private String name;
}