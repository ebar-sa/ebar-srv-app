package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.model.Review;
import com.ebarapp.ebar.model.dtos.NewReviewDTO;
import com.ebarapp.ebar.model.dtos.ReviewDTO;
import com.ebarapp.ebar.model.dtos.ReviewItemsDTO;
import com.ebarapp.ebar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final BarService barService;
    private final BarTableService barTableService;
    private final ItemMenuService itemMenuService;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, BarService barService, ItemMenuService itemMenuService,
                            UserService userService, BarTableService barTableService) {
        this.reviewService = reviewService;
        this.barService = barService;
        this.itemMenuService = itemMenuService;
        this.userService = userService;
        this.barTableService = barTableService;
    }

    @GetMapping("/{token}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReviewItemsDTO> getAvailableItemsToReview(@PathVariable String token) {
        var barTable = this.barTableService.getBarTableByToken(token);
        if (barTable == null) {
            return ResponseEntity.notFound().build();
        }

        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var client = this.userService.getClientByUsername(ud.getUsername());
        if (client == null || !barTable.getClients().contains(client)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var itemsReviewed = this.itemMenuService.getItemMenusReviewedByUsername(client.getUsername());

        var reviewItems = new ReviewItemsDTO();
        reviewItems.setBarReviewed(barTable.getBar().getReviews().stream().anyMatch(x -> x.getCreator().equals(client)));
        reviewItems.setTableId(barTable.getId());
        reviewItems.setItems(barTable.getBill().getItemBill().stream()
                .map(ItemBill::getItemMenu)
                .filter(x -> !itemsReviewed.contains(x))
                .collect(Collectors.toSet()));

        return ResponseEntity.ok(reviewItems);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Review> createReview(@RequestBody NewReviewDTO newReview) {
        if (newReview.getBar() == null && (newReview.getItems() == null || newReview.getItems().isEmpty())) {
            return ResponseEntity.badRequest().build();
        }

        var bar = this.barService.findBarById(newReview.getBarId());
        if (bar == null || bar.getBarTables().stream().noneMatch(t -> t.getToken().equals(newReview.getTableToken()))) {
            return ResponseEntity.notFound().build();
        }

        Map<ItemMenu, ReviewDTO> itemReviews = new HashMap<>();

        if(newReview.getItems() != null) {
            for (Integer itemId : newReview.getItems().keySet()) {
                var itemMenu = this.itemMenuService.getById(itemId);
                if (itemMenu == null) {
                    return ResponseEntity.notFound().build();
                } else {
                    itemReviews.put(itemMenu, newReview.getItems().get(itemId));
                }
            }
        }

        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ud.getUsername();
        var client = this.userService.getClientByUsername(username);

        if(newReview.getBar() != null) {
            var review = this.reviewService.saveReview(newReview.getBar(), client);
            bar.addReview(review);
        }

        if(newReview.getItems() != null) {
            for (Map.Entry<ItemMenu, ReviewDTO> entry : itemReviews.entrySet()) {
                var review = this.reviewService.saveReview(entry.getValue(), client);
                entry.getKey().addReview(review);
            }
        }

        return ResponseEntity.created(URI.create("")).build();
    }
}
