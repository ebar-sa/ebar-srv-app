package com.ebarapp.ebar.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="cuenta")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "codigo")
    private String codigo;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Mesa mesa;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "cuenta_item_carta", joinColumns = @JoinColumn(name = "cuenta_id"), inverseJoinColumns = @JoinColumn(name = "item_carta_id"))
    private Set<ItemCarta> itemCartas;
}

