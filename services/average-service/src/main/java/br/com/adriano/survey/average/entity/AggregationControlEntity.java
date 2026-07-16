package br.com.adriano.survey.average.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "aggregation_control")
public class AggregationControlEntity {

    @Id
    private String id;

    private LocalDate lastProcessedDate;

    private Instant updatedAt;

    private AggregationStatus status;
}
