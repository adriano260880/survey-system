package br.com.adriano.survey.review.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
@CompoundIndex(
        name = "idx_order_unique",
        def = "{'orderId':1}",
        unique = true
)

@CompoundIndex(
        name = "idx_pending_reviews",
        def = "{'processedAt':1,'createdAt':1,'restaurantId':1}"
)

@CompoundIndex(
        name = "idx_restaurant_recent",
        def = "{'restaurantId':1,'createdAt':-1}"
)
public class ReviewEntity {

    @Id
    private String id;
    private Long orderId;
    private Long restaurantId;
    private Long userId;
    private Integer rating;
    private Instant createdAt;
    private Instant processedAt;

}
