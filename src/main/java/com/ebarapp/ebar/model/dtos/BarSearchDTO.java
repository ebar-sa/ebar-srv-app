package com.ebarapp.ebar.model.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarSearchDTO {

    private Integer id;
    private String name;
    private String location;

    public BarSearchDTO() {
        //Empty
    }

    public BarSearchDTO(Integer id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }
}
