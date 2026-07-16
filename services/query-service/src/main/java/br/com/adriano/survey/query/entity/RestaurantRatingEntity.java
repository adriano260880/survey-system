package br.com.adriano.survey.query.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "restaurant_rating")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRatingEntity {

    @Id
    private Long restaurantId;
    private Long ratingSum;
    private Long totalReviews;
    private BigDecimal average;
    private LocalDate lastProcessedDate;
    private Instant updatedAt;
}
