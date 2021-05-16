package com.ebarapp.ebar.model.dtos;

import com.ebarapp.ebar.model.ItemMenu;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ReviewItemsDTO {

    private Integer tableId;
    private Set<ItemMenu> items;
    private Boolean barReviewed;
}
