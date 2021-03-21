package com.ebarapp.ebar.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="bill")
public class Bill extends BaseEntity {

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private BarTable table;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<ItemMenu> itemMenu;
}

