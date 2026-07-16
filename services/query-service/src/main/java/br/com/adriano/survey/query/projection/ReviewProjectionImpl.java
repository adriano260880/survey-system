package br.com.adriano.survey.query.projection;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ReviewProjectionImpl implements ReviewProjection {

    private Long orderId;
    private Long restaurantId;
    private Long userId;
    private Integer rating;
    private Instant createdAt;
}
