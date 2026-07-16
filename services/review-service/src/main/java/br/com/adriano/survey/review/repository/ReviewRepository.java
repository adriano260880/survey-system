package br.com.adriano.survey.review.repository;

import br.com.adriano.survey.review.entity.ReviewEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<ReviewEntity, String> {

    boolean existsByOrderId(Long orderId);
}
