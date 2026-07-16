package br.com.adriano.survey.average.entity;

import java.time.Instant;


public record ReviewProjection(
        Long restaurantId,
        Integer rating,
        Instant createdAt,
        Instant processedAt
) {

}