package main.java.com.ebarapp.ebar.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "votacion")
    private Set<Opcion> opciones;

}

