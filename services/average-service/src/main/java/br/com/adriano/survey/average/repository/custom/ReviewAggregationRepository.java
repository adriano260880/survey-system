package br.com.adriano.survey.average.repository.custom;

import br.com.adriano.survey.average.dto.RestaurantAggregationDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewAggregationRepository {

    List<RestaurantAggregationDto> aggregate(LocalDate day);
    Optional<LocalDate> findFirstReviewDate();
}
