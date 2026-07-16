package br.com.adriano.survey.query.repository;

import br.com.adriano.survey.query.projection.ReviewProjection;
import org.springframework.data.domain.Page;

public interface ReviewRepository {

    Page<ReviewProjection> findByRestaurantId(
            Long restaurantId,
            int page,
            int size
    );
}
