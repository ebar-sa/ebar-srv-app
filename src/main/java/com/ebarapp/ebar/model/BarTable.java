package com.ebarapp.ebar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({ "bar", "bill", "client" })
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
    @Column(name="seats")
    private Integer seats;
    
    @NotNull
    @ManyToOne
    private Bar bar;
    
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="bill_id")
    private Bill bill;
    
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="client_username")
    private Client client;

}