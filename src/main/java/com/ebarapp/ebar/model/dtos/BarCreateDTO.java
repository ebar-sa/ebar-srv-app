package com.ebarapp.ebar.model.dtos;

import com.ebarapp.ebar.model.DBImage;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class BarCreateDTO {

    private String name;
    private String description;
    private String contact;
    private String location;
    private Date openingTime;
    private Date closingTime;
    private Set<DBImage> images;

    public BarCreateDTO() {
        //Empty
    }

    public BarCreateDTO(String name, String description, String contact, String location, Date openingTime,
                        Date closingTime, Set<DBImage> images, String ownerUsername) {
        this.name = name;
        this.description = description;
        this.contact = contact;
        this.location = location;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.images = images;
    }
}
