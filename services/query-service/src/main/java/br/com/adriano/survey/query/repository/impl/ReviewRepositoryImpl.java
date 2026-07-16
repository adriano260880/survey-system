package br.com.adriano.survey.query.repository.impl;

import br.com.adriano.survey.query.projection.ReviewProjection;
import br.com.adriano.survey.query.projection.ReviewProjectionImpl;
import br.com.adriano.survey.query.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ReviewProjection> findByRestaurantId(Long restaurantId, int page, int size) {

        Query query = new Query();

        query.addCriteria(
                Criteria.where("restaurantId")
                        .is(restaurantId)
        );

        long total =
                mongoTemplate.count(
                        query,
                        "reviews"
                );

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                );

        query.with(pageable);

        query.fields()
                .include("orderId")
                .include("restaurantId")
                .include("userId")
                .include("rating")
                .include("createdAt");


        List<ReviewProjectionImpl> content =
                mongoTemplate.find(
                        query,
                        ReviewProjectionImpl.class,
                        "reviews"
                );


        return new PageImpl<>(
                List.copyOf(content),
                pageable,
                total
        );
    }
}
