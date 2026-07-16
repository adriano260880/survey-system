package br.com.adriano.survey.average.service;

import br.com.adriano.survey.average.dto.RestaurantAggregationDto;
import br.com.adriano.survey.average.entity.AggregationControlEntity;
import br.com.adriano.survey.average.entity.AggregationStatus;
import br.com.adriano.survey.average.entity.RestaurantRatingEntity;
import br.com.adriano.survey.average.repository.AggregationControlRepository;
import br.com.adriano.survey.average.repository.RestaurantRatingRepository;
import br.com.adriano.survey.average.repository.custom.ReviewAggregationRepository;
import br.com.adriano.survey.average.repository.custom.ReviewUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantRatingAggregationService {

    private static final String CONTROL_ID = "restaurant-average";

    private final AggregationControlRepository aggregationControlRepository;
    private final RestaurantRatingRepository restaurantRatingRepository;
    private final ReviewAggregationRepository reviewAggregationRepository;
    private final ReviewUpdateRepository reviewUpdateRepository;

    public void aggregate() {

        log.info("Starting restaurant rating aggregation");

        AggregationControlEntity control = loadControl();

        control.setStatus(AggregationStatus.RUNNING);
        control.setUpdatedAt(Instant.now());

        aggregationControlRepository.save(control);

        try {

            LocalDate yesterday = LocalDate.now().minusDays(1);

            LocalDate nextDay = resolveNextDay(control);

            while (!nextDay.isAfter(yesterday)) {

                log.info("Processing {}", nextDay);

                processDay(nextDay);

                control.setLastProcessedDate(nextDay);

                nextDay = nextDay.plusDays(1);
            }

            control.setStatus(AggregationStatus.SUCCESS);
            control.setUpdatedAt(Instant.now());

            aggregationControlRepository.save(control);

            log.info("Aggregation finished successfully");

        } catch (Exception ex) {

            log.error("Aggregation failed", ex);

            control.setStatus(AggregationStatus.FAILED);
            control.setUpdatedAt(Instant.now());

            aggregationControlRepository.save(control);

            throw ex;
        }

    }

    private AggregationControlEntity loadControl() {

        return aggregationControlRepository.findById(CONTROL_ID)
                .orElseGet(() ->
                        aggregationControlRepository.save(
                                AggregationControlEntity.builder()
                                        .id(CONTROL_ID)
                                        .status(AggregationStatus.SUCCESS)
                                        .updatedAt(Instant.now())
                                        .build()
                        )
                );

    }

    private LocalDate resolveNextDay(AggregationControlEntity control) {

        if (control.getLastProcessedDate() != null) {
            return control.getLastProcessedDate().plusDays(1);
        }

        return reviewAggregationRepository.findFirstReviewDate()
                .orElse(LocalDate.now());

    }

    private void processDay(LocalDate day) {

        List<RestaurantAggregationDto> aggregations =
                reviewAggregationRepository.aggregate(day);

        log.info("Restaurants found: {}", aggregations.size());

        aggregations.forEach(dto ->
                log.info("DTO -> {}", dto));

        updateRestaurantRatings(aggregations, day);

        reviewUpdateRepository.markAsProcessed(day);

    }

    private void updateRestaurantRatings(List<RestaurantAggregationDto> aggregations,
                                         LocalDate day) {

        aggregations.forEach(dto -> updateRestaurantRating(dto, day));

    }

    private void updateRestaurantRating(RestaurantAggregationDto dto,
                                        LocalDate day) {

        RestaurantRatingEntity entity =
                restaurantRatingRepository.findById(dto.getRestaurantId())
                        .orElse(
                                RestaurantRatingEntity.builder()
                                        .restaurantId(dto.getRestaurantId())
                                        .ratingSum(0L)
                                        .totalReviews(0L)
                                        .build()
                        );

        long ratingSum =
                entity.getRatingSum() + dto.getRatingSum();

        long totalReviews =
                entity.getTotalReviews() + dto.getTotalReviews();

        BigDecimal average =
                BigDecimal.valueOf(ratingSum)
                        .divide(
                                BigDecimal.valueOf(totalReviews),
                                2,
                                RoundingMode.HALF_UP
                        );

        entity.setRatingSum(ratingSum);
        entity.setTotalReviews(totalReviews);
        entity.setAverage(average);
        entity.setLastProcessedDate(day);
        entity.setUpdatedAt(Instant.now());

        restaurantRatingRepository.save(entity);

    }
}
