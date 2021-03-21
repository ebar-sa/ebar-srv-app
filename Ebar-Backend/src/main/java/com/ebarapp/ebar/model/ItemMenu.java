package com.ebarapp.ebar.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.jetbrains.annotations.NotNull;

import com.ebarapp.ebar.model.type.RationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="item_menu")
public class ItemMenu extends BaseEntity {

    @NotNull
    @ManyToOne
    private Category category;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ration_type")
    private RationType rationType;

    @NotNull
    @Column(name = "price")
    private Double price;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private DBImage image;  

}

