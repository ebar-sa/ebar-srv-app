package com.ebarapp.ebar.model;


import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="trabajador")
public class Trabajador extends Usuario {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "votacion_id")
    private Votacion votacion;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "bar_id")
    private Bar bar;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "trabajador")
    private Set<Mesa> mesas;
}
