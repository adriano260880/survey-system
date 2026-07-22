package br.com.adriano.survey.query.repository;

import br.com.adriano.survey.query.dto.ReviewSliceResponse;
import br.com.adriano.survey.query.projection.ReviewProjection;
import org.springframework.data.domain.Page;

import java.time.Instant;

public interface ReviewRepository {

    Page<ReviewProjection> findByRestaurantId(
            Long restaurantId,
            int page,
            int size
    );

    ReviewSliceResponse findSlice(
            Long restaurantId,
            Instant lastCreatedAt,
            int size
    );
}
