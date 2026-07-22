package br.com.adriano.survey.query.dto;

import java.time.Instant;
import java.util.List;

public record ReviewSliceResponse(
        List<ReviewResponse> content,
        boolean hasNext,
        Instant nextCursor
) {
}
