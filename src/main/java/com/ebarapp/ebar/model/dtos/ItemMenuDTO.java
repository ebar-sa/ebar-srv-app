package com.ebarapp.ebar.model.dtos;

import com.ebarapp.ebar.model.DBImage;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.model.Review;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Getter
@Setter
public class ItemMenuDTO {

    @NotNull
    private String		name;

    private String		description;

    @NotNull
    private String rationType;

    @NotNull
    private Double		price;

    @NotNull
    private String category;

    private DBImage image;

    private Set<Review> reviews;

    private Double avgRating;

    public ItemMenuDTO(ItemMenu itemMenu) {
        this.name = itemMenu.getName();
        this.description = itemMenu.getDescription();
        this.rationType = itemMenu.getRationType();
        this.price = itemMenu.getPrice();
        this.category = itemMenu.getCategory();
        this.image = itemMenu.getImage();
        this.reviews = itemMenu.getReviews();
        var bd = BigDecimal.valueOf(reviews.stream().mapToDouble(Review::getValue).average().orElse(0.));
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        this.avgRating = bd.doubleValue();
    }
}
