package main.java.com.ebarapp.ebar.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name="empresa")
public class Empresa extends Usuario{

    @NotNull
    @Column(name = "cif")
    private String cif;
}
