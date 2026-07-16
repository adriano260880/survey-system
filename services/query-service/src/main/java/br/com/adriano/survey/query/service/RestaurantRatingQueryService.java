package br.com.adriano.survey.query.service;

import br.com.adriano.survey.query.dto.RestaurantRatingResponse;
import br.com.adriano.survey.query.entity.RestaurantRatingEntity;
import br.com.adriano.survey.query.repository.RestaurantRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RestaurantRatingQueryService {

    private static final String PREFIX = "rating:";

    private final RedisTemplate<String, RestaurantRatingResponse> redisTemplate;

    private final RestaurantRatingRepository repository;

    public RestaurantRatingResponse findByRestaurant(Long restaurantId) {

        String key = PREFIX + restaurantId;

        RestaurantRatingResponse cache =
                redisTemplate.opsForValue().get(key);

        if (cache != null) {
            return cache;
        }

        RestaurantRatingEntity entity =
                repository.findById(restaurantId)
                        .orElseThrow(
                                () -> new RuntimeException("Restaurant not found.")
                        );

        RestaurantRatingResponse response =
                RestaurantRatingResponse.builder()
                        .restaurantId(entity.getRestaurantId())
                        .average(entity.getAverage())
                        .totalReviews(entity.getTotalReviews())
                        .lastProcessedDate(entity.getLastProcessedDate())
                        .build();

        redisTemplate.opsForValue()
                .set(key, response, Duration.ofHours(24));

        return response;
    }
}
