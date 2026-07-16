package br.com.adriano.survey.average.repository;

import br.com.adriano.survey.average.entity.RestaurantRatingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RestaurantRatingRepository extends MongoRepository<RestaurantRatingEntity, Long> {

}
