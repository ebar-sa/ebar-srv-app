package com.ebarapp.ebar.model.dtos;

import com.ebarapp.ebar.model.Option;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;


@Getter
@Setter
public class VotingDTO {

    private String title;

    private String description;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime openingHour;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime closingHour;

    private Integer timer;

    private Set<Option> options;

    private Set<String> votersUsernames;
}
