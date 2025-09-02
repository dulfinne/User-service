package com.dulfinne.randomgame.userservice.service.impl;

import com.dulfinne.randomgame.userservice.dto.request.MoneyRequest;
import com.dulfinne.randomgame.userservice.entity.GamePayment;
import com.dulfinne.randomgame.userservice.exception.EntityAlreadyExistsException;
import com.dulfinne.randomgame.userservice.kafka.entity.Payment;
import com.dulfinne.randomgame.userservice.repository.GamePaymentRepository;
import com.dulfinne.randomgame.userservice.service.PaymentService;
import com.dulfinne.randomgame.userservice.service.UserService;
import com.dulfinne.randomgame.userservice.util.ExceptionKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final UserService userService;
    private final GamePaymentRepository gamePaymentRepository;

    @Override
    @Transactional
    public Mono<Void> processPayment(Payment payment) {
        return checkGamePaymentUniqueness(payment.gameId())
                .then(Mono.just(new MoneyRequest(payment.amount())))
                .flatMap(
                        money ->
                                Boolean.TRUE.equals(payment.positiveFlag())
                                        ? userService.creditMoney(payment.username(), money)
                                        : userService.debitMoney(payment.username(), money))
                .flatMap(ignored -> gamePaymentRepository.save(new GamePayment(payment.gameId())))
                .then();
    }

    private Mono<Void> checkGamePaymentUniqueness(String gameId) {
        return gamePaymentRepository
                .findById(gameId)
                .flatMap(
                        payment ->
                                Mono.error(
                                        new EntityAlreadyExistsException(
                                                String.format(ExceptionKeys.GAME_PAYMENT_EXISTS_USERNAME, gameId))))
                .switchIfEmpty(Mono.empty())
                .then();
    }
}
