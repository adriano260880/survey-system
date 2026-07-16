package br.com.adriano.survey.average.scheduler;

import br.com.adriano.survey.average.service.RestaurantRatingAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AggregationScheduler {

    private final RestaurantRatingAggregationService service;

    @Scheduled(cron = "${aggregation.scheduler.cron}")
    public void execute() {
        service.aggregate();
    }
}
