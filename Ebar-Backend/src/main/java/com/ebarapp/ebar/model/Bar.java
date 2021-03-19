package com.ebarapp.ebar.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="bar")
public class Bar {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(name = "nombre")
	private String nombre;

	@NotNull
	@Column(name = "descripcion")
	private String descripcion;

	@NotNull
	@Column(name="contacto")
	private String contacto;

	@NotNull
	@Column(name = "ubicacion")
	private String ubicacion;

	//@NotNull
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "bar", fetch = FetchType.LAZY)
	private Set<Imagen> imagenes;

	//@NotNull
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "bar", fetch = FetchType.LAZY)
	private Set<ItemCarta> itemsCarta;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "empresa_id")
	private Empresa empresa;
	

}