package br.com.adriano.survey.average.repository.impl;

import br.com.adriano.survey.average.dto.RestaurantAggregationDto;
import br.com.adriano.survey.average.entity.ReviewProjection;
import br.com.adriano.survey.average.repository.custom.ReviewAggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewAggretationRespositoryImpl implements ReviewAggregationRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<RestaurantAggregationDto> aggregate(LocalDate day) {

        Instant start = day.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = day.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        MatchOperation match =
                Aggregation.match(
                        Criteria.where("createdAt")
                                .gte(start)
                                .lt(end)
                                .and("processedAt").is(null)
                );

        GroupOperation group =
                Aggregation.group("restaurantId")
                        .count().as("totalReviews")
                        .sum("rating").as("ratingSum");

        ProjectionOperation projection =
                Aggregation.project()
                        .and("_id").as("restaurantId")
                        .and("ratingSum").as("ratingSum")
                        .and("totalReviews").as("totalReviews");

        Aggregation aggregation =
                Aggregation.newAggregation(
                        match,
                        group,
                        projection
                );

        return mongoTemplate.aggregate(
                aggregation,
                "reviews",
                RestaurantAggregationDto.class
        ).getMappedResults();
    }

    @Override
    public Optional<LocalDate> findFirstReviewDate() {
        Query query = new Query();

        query.with(Sort.by(Sort.Direction.ASC, "createdAt"));

        query.limit(1);

        query.fields().include("createdAt");

        ReviewProjection review =
                mongoTemplate.findOne(
                        query,
                        ReviewProjection.class
                );

        if (review == null) {
            return Optional.empty();
        }

        return Optional.of(
                review.createdAt()
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()
        );
    }
}
