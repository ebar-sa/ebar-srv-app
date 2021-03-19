package com.ebarapp.ebar.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="basic")
public class Basic extends Usuario{

    @ManyToOne
    @JoinColumn(name = "votacion_id")
    private Votacion votacion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "basic_cuenta", joinColumns = @JoinColumn(name = "basic_id"), inverseJoinColumns = @JoinColumn(name = "votacion_id"))
    private Set<Cuenta> cuentas;
}
