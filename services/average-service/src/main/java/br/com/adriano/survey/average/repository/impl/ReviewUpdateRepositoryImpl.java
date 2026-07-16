package br.com.adriano.survey.average.repository.impl;

import br.com.adriano.survey.average.entity.ReviewProjection;
import br.com.adriano.survey.average.repository.custom.ReviewUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Repository
@RequiredArgsConstructor
public class ReviewUpdateRepositoryImpl implements ReviewUpdateRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void markAsProcessed(LocalDate day) {

        Instant start =
                day.atStartOfDay(ZoneOffset.UTC).toInstant();

        Instant end =
                day.plusDays(1)
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant();

        Query query = new Query();

        query.addCriteria(
                Criteria.where("createdAt")
                        .gte(start)
                        .lt(end)
        );

        query.addCriteria(
                Criteria.where("processedAt").is(null)
        );

        Update update = new Update();

        update.set("processedAt", Instant.now());

        mongoTemplate.updateMulti(
                query,
                update,
                ReviewProjection.class
        );
    }
}
