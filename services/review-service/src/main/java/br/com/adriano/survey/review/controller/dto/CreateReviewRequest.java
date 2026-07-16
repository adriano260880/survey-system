package br.com.adriano.survey.review.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(

        @NotNull
        Long orderId,

        @NotNull
        Long restaurantId,

        @NotNull
        Long userId,

        @NotNull
        @Min(1)
        @Max(5)
        Integer rating

) {}
