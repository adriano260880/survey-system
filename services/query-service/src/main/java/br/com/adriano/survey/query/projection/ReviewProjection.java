package br.com.adriano.survey.query.projection;

import java.time.Instant;

public interface ReviewProjection {

    Long getOrderId();
    Long getRestaurantId();
    Long getUserId();
    Integer getRating();
    Instant getCreatedAt();

}
