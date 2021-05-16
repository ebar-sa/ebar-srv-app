package com.ebarapp.ebar.model.dtos;

import com.ebarapp.ebar.model.DBImage;
import com.ebarapp.ebar.model.Employee;

import com.ebarapp.ebar.model.Review;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
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
    private List<Review> reviews;
    private Double avgRating;

    public BarDTO() {
        //Empty
    }

    public BarDTO(Integer id, String name, String description, String contact, String location, Date openingTime, Date closingTime, Set<DBImage> images,
                  Integer tables, Integer freeTables, String ownerUsername, Set<Employee> employees, List<Review> reviews) {
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
        this.reviews = reviews;
        var bd = BigDecimal.valueOf(reviews.stream().mapToDouble(Review::getValue).average().orElse(0.));
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        this.avgRating = bd.doubleValue();
    }
}
