package br.com.adriano.survey.query.repository;

import br.com.adriano.survey.query.entity.RestaurantRatingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RestaurantRatingRepository
extends MongoRepository<RestaurantRatingEntity, Long> {
}
