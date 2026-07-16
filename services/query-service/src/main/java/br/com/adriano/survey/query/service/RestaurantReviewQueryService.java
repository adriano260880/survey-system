package br.com.adriano.survey.query.service;

import br.com.adriano.survey.query.dto.ReviewResponse;
import br.com.adriano.survey.query.projection.ReviewProjection;
import br.com.adriano.survey.query.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantReviewQueryService {

    private final ReviewRepository reviewRepository;

    public Page<ReviewResponse> findReviews(
            Long restaurantId,
            int page,
            int size
    ) {
        return reviewRepository
                .findByRestaurantId(restaurantId, page, size)
                .map(this::toResponse);
    }

    private ReviewResponse toResponse(
            ReviewProjection projection
    ) {
        return ReviewResponse.builder()
                .orderId(projection.getOrderId())
                .userId(projection.getUserId())
                .rating(projection.getRating())
                .createdAt(projection.getCreatedAt())
                .build();
    }
}
