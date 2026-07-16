package br.com.adriano.survey.average.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestaurantAggregationDto {

    private Long restaurantId;
    private Long ratingSum;
    private Long totalReviews;
    private BigDecimal average;

}
