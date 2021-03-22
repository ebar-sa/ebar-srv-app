package com.ebarapp.ebar.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="bar_table")
public class BarTable extends BaseEntity {

    @NotNull
    @Column(name = "name")
    private String name;
    
    @NotNull
    @Column(name = "token")
    private String token;
    
    @NotNull
    @Column(name = "free")
    private boolean free;

    @NotNull
    @ManyToOne
    private Bar bar;
    
	@OneToOne(fetch = FetchType.LAZY)
    private Bill bill;
    
	@OneToOne(fetch = FetchType.LAZY)
    private Client client;
}