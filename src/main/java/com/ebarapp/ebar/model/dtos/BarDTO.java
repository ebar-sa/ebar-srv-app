package com.ebarapp.ebar.model.dtos;

import com.ebarapp.ebar.model.DBImage;
import com.ebarapp.ebar.model.Employee;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class BarDTO {

    private Integer id;
    private String name;
    private String description;
    private String contact;
    private String location;
    private Date openingTime;
    private Date closingTime;
    private Set<DBImage> images;
    private Integer tables;
    private Integer freeTables;
    private String owner;
    private Set<Employee> employees;

    public BarDTO() {
        //Empty
    }

    public BarDTO(Integer id, String name, String description, String contact, String location, Date openingTime, Date closingTime, Set<DBImage> images,
                  Integer tables, Integer freeTables, String ownerUsername, Set<Employee> employees) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.contact = contact;
        this.location = location;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.images = images;
        this.tables = tables;
        this.freeTables = freeTables;
        this.owner = ownerUsername;
        this.employees = employees;
    }
}
