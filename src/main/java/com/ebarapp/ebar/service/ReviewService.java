package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.Client;
import com.ebarapp.ebar.model.Review;
import com.ebarapp.ebar.model.dtos.ReviewDTO;
import com.ebarapp.ebar.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;


    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review saveReview(ReviewDTO reviewDTO, Client client) {
        var review = new Review();
        review.setValue(reviewDTO.getRating());
        review.setDescription(reviewDTO.getDescription());
        review.setCreationDate(LocalDate.now());
        review.setCreator(client);
        return this.reviewRepository.save(review);
    }
}
