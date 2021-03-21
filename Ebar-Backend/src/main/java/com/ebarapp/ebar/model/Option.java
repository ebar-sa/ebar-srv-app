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
@Table(name="v_option")
public class Option extends BaseEntity {
    
	@NotNull
    @Column(name = "description")
    private String description;
	
	@NotNull
    @Column(name = "votes")
    private Integer votes;

}