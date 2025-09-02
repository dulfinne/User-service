package com.dulfinne.randomgame.userservice.service.impl;

import com.dulfinne.randomgame.userservice.dto.request.MoneyRequest;
import com.dulfinne.randomgame.userservice.kafka.entity.Payment;
import com.dulfinne.randomgame.userservice.service.PaymentService;
import com.dulfinne.randomgame.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  private final UserService userService;

  @Override
  public Mono<Void> processPayment(Payment payment) {
    MoneyRequest money = new MoneyRequest(payment.amount());

    return Boolean.TRUE.equals(payment.positiveFlag())
        ? userService.creditMoney(payment.username(), money).then()
        : userService.debitMoney(payment.username(), money).then();
  }
}
