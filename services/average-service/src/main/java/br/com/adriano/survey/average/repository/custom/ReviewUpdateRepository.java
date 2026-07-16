package br.com.adriano.survey.average.repository.custom;

import java.time.LocalDate;

public interface ReviewUpdateRepository {

    void markAsProcessed(LocalDate day);
}
