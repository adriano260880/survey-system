package br.com.adriano.survey.review.service;

import br.com.adriano.survey.review.entity.ReviewEntity;
import br.com.adriano.survey.review.exception.ReviewAlreadyExistsException;
import br.com.adriano.survey.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewEntity create(ReviewEntity review) {

        review.setCreatedAt(Instant.now());
        try {
            return repository.save(review);
        } catch (DuplicateKeyException ex) {
            throw new ReviewAlreadyExistsException(review.getOrderId());
        }
    }
}
