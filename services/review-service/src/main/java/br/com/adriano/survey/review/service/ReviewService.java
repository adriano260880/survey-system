package br.com.adriano.survey.review.service;

import br.com.adriano.survey.review.entity.ReviewEntity;
import br.com.adriano.survey.review.exception.ReviewAlreadyExistsException;
import br.com.adriano.survey.review.repository.ReviewRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReviewService {

    private final ReviewRepository repository;

    private final MeterRegistry meterRegistry;

    private final Counter reviewsCreatedCounter;

    public ReviewService(ReviewRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;

        this.reviewsCreatedCounter =
        Counter.builder("reviews_created_total")
                .description("Total reviews Created")
                .register(meterRegistry);
    }

    @Timed(
            value = "review_creation_duration",
            description = "Time spend creating review"
    )
    public ReviewEntity create(ReviewEntity review) {

        validate(review);
        review.setCreatedAt(Instant.now());
        try {
            reviewsCreatedCounter.increment();
            incrementRatingCounter(review.getRating());
            return repository.save(review);
        } catch (DuplicateKeyException ex) {
            throw new ReviewAlreadyExistsException(review.getOrderId());
        }
    }

    private void incrementRatingCounter(Integer rating) {
        Counter.builder("reviews_by_rating_total")
                .description("Reviews_grouped by rating")
                .tag("rating", rating.toString())
                .register(meterRegistry)
                .increment();
    }

    private void validate(ReviewEntity review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            Counter.builder("reviews_validation_errors_total")
                    .description("Validation Errors")
                    .tag("reason", "invalid-rating")
                    .register(meterRegistry)
                    .increment();

            throw new IllegalArgumentException("Invalid rating");
        }
    }
}
