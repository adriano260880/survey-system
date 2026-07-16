package br.com.adriano.survey.review.controller;


import br.com.adriano.survey.review.controller.dto.CreateReviewRequest;
import br.com.adriano.survey.review.controller.dto.ReviewResponse;
import br.com.adriano.survey.review.entity.ReviewEntity;
import br.com.adriano.survey.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(@Valid @RequestBody CreateReviewRequest request) {

        ReviewEntity review = ReviewEntity.builder()
                .orderId(request.orderId())
                .restaurantId(request.restaurantId())
                .userId(request.userId())
                .rating(request.rating())
                .build();

        ReviewEntity saved = service.create(review);

        return new ReviewResponse(saved.getId());
    }
}
