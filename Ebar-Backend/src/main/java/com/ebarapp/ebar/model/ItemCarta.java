package main.java.com.ebarapp.ebar.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Entity
@Table(name="item_carta")
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
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "item_carta")
    private Imagen imagen;

}

