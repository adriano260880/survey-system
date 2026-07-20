package br.com.adriano.survey.query.controller;

import br.com.adriano.survey.query.dto.RestaurantRatingResponse;
import br.com.adriano.survey.query.dto.ReviewResponse;
import br.com.adriano.survey.query.service.RestaurantRatingQueryService;
import br.com.adriano.survey.query.service.RestaurantReviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("restaurants")
@RequiredArgsConstructor
public class RestaurantQueryController {

    private final RestaurantRatingQueryService ratingService;
    private final RestaurantReviewQueryService queryService;

    @GetMapping("{restaurantId}")
    public RestaurantRatingResponse find(
            @PathVariable Long restaurantId
    ) {
        return ratingService.findByRestaurant(restaurantId);
    }

    @GetMapping("{restaurantId}/reviews")
    public Page<ReviewResponse> findReview(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return queryService.findReviews(
                restaurantId,
                page,
                size
        );
    }
}
