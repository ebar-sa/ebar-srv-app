package com.ebarapp.ebar.model.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
public class NewReviewDTO {

    private ReviewDTO bar;
    private Map<Integer, ReviewDTO> items;
    @NotNull
    private String tableToken;
    @NotNull
    private Integer barId;
}
