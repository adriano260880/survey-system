package br.com.adriano.survey.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRatingResponse {

    private Long restaurantId;
    private BigDecimal average;
    private Long totalReviews;
    private LocalDate lastProcessedDate;
}
