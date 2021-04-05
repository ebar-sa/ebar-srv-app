package com.ebarapp.ebar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import com.ebarapp.ebar.model.dtos.OptionDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="v_option")
public class Option extends BaseEntity {

    public Option() {}

    public Option(OptionDTO optionDTO) {
        this.description = optionDTO.getDescription();
        this.votes = optionDTO.getVotes();
    }
    
	@NotNull
    @Column(name = "description")
    private String description;
	
	@NotNull
    @Column(name = "votes")
    private Integer votes;

}