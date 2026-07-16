package br.com.adriano.survey.average.repository;

import br.com.adriano.survey.average.entity.AggregationControlEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AggregationControlRepository extends MongoRepository<AggregationControlEntity, String> {

}
