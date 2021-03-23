package com.ebarapp.ebar.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="voting")
public class Voting extends BaseEntity {

    @NotNull
    @Column(name = "title")
    private String title;

    @NotNull
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "opening_hour")
    private LocalDateTime openingHour;

    @Column(name = "closing_hour")
    private LocalDateTime closingHour;
    
    @Column(name = "timer")
    private Integer timer;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Option> options;
    
    @ElementCollection(targetClass=String.class)
    private Set<String> votersUsernames;

}

