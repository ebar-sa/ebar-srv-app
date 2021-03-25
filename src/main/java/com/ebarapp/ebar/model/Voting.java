package com.ebarapp.ebar.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime openingHour;

    @Column(name = "closing_hour")
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime closingHour;
    
    @Column(name = "timer")
    private Integer timer;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Option> options;
    
    @ElementCollection(targetClass=String.class)
    private Set<String> votersUsernames;

    public void addVoter(String username){ getVotersUsernames().add(username);}

    public void addOption(Option newOption) {
        getOptions().add(newOption);
    }

    public void deleteOption(Option option) { getOptions().remove(option); }

}

