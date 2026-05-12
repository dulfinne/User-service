package com.dulfinne.randomgame.userservice.service;

import com.dulfinne.randomgame.userservice.kafka.entity.Payment;
import reactor.core.publisher.Mono;

public interface PaymentService {
  Mono<Void> processPayment(Payment payment);
}
