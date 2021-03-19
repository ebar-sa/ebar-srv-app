package com.ebarapp.ebar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Entity
@Table(name="imagen")
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "filename")
    private String fileName;

    @NotNull
    @Column(name = "filetype")
    private String fileType;

    @NotNull
    @Column(name = "ruta")
    private String ruta;
    
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name ="bar_id", referencedColumnName = "id")
    private Bar bar;
    
    @OneToOne
    @JoinColumn(name="itemCarta_id")
    private ItemCarta itemCarta;
}

