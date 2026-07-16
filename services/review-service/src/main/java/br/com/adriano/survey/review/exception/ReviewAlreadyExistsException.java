package br.com.adriano.survey.review.exception;

public class ReviewAlreadyExistsException extends RuntimeException {

    public ReviewAlreadyExistsException(Long orderId) {
        super("Avaliação pedido %d já realizada.".formatted(orderId));
    }
}
