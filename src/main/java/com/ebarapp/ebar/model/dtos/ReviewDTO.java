package com.ebarapp.ebar.model.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ReviewDTO {

    @NotNull
    @Max(value = 5)
    @Min(value = 0)
    private Double rating;

    private String description;

}
