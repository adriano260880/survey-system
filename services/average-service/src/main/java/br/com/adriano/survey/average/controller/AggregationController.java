package br.com.adriano.survey.average.controller;

import br.com.adriano.survey.average.service.RestaurantRatingAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("internal/aggregation")
@RequiredArgsConstructor
public class AggregationController {

    private final RestaurantRatingAggregationService service;

    @PostMapping("run")
    public ResponseEntity<Void> run() {
        service.aggregate();

        return ResponseEntity.accepted().build();
    }
}
