package com.ebarapp.ebar.model;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="votacion")
public class Votacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "titulo")
    private String titulo;

    @NotNull
    @Column(name = "descripcion")
    private String descripcion;

    @NotNull
    @Column(name = "inicio")
    private LocalDateTime inicio;

    @Column(name = "fin")
    private LocalDateTime fin;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "bar_id")
    private Bar bar;

    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Opcion> opciones;

}

