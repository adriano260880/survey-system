package br.com.adriano.survey.query.repository.impl;

import br.com.adriano.survey.query.dto.ReviewResponse;
import br.com.adriano.survey.query.dto.ReviewSliceResponse;
import br.com.adriano.survey.query.projection.ReviewProjection;
import br.com.adriano.survey.query.projection.ReviewProjectionImpl;
import br.com.adriano.survey.query.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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

    @Override
    public ReviewSliceResponse findSlice(Long restaurantId, Instant lastCreatedAt, int size) {

        Query query = Query.query(
                Criteria.where("restaurantId")
                        .is(restaurantId)
        );

        if (lastCreatedAt != null) {

            query.addCriteria(
                    Criteria.where("createdAt")
                            .lt(lastCreatedAt)
            );
        }

        query.with(
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        query.limit(size + 1);

        List<ReviewResponse> result =
                mongoTemplate.find(
                        query,
                        ReviewResponse.class,
                        "reviews"
                );

        if (result.isEmpty()) {
            return new ReviewSliceResponse(
                    List.of(),
                    false,
                    null
            );
        }

        boolean hasNext = result.size() > size;

        if (hasNext) {
            result.remove(result.size() - 1);
        }

        Instant nextCursor = hasNext
                ? result.get(result.size() - 1).getCreatedAt()
                : null;

        return new ReviewSliceResponse(
                result,
                hasNext,
                nextCursor
        );
    }
}
