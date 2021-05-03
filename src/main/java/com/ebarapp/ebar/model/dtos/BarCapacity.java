package com.ebarapp.ebar.model.dtos;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarCapacity {

    private Integer id;
    private String name;
    private String capacity;
    private String location;
    private Map<String, BigDecimal> coord;
    private Double distance;

}
