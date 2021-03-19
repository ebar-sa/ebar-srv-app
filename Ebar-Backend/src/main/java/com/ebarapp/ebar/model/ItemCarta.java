package com.ebarapp.ebar.model;

import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Entity
@Table(name="itemCarta")
public class ItemCarta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "categoria")
    private Categoria categoria;

    @NotNull
    @Column(name = "nombre")
    private String nombre;

    @NotNull
    @Column(name = "descripcion")
    private String descripcion;

    @NotNull
    @Column(name = "tipo")
    private Tipo tipo;

    @NotNull
    @Column(name = "precio")
    private Double precio;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "itemCarta")
    private Imagen imagen;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="bar_id",referencedColumnName = "id")
    private Bar bar; 
    
    @ManyToMany(mappedBy = "itemCartas")
    private List<Cuenta> cuentas;
    

}

